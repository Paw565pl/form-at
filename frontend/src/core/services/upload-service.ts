import { getClientEnv } from "@/core/lib/env/client-env";
import { serverEnv } from "@/core/lib/env/server-env";
import { apiService } from "@/core/services/api-service";
import axios from "axios";

interface BatchUploadRequestDto {
  readonly files: {
    readonly filename: string;
  }[];
}

interface UploadRequestResponseDto {
  readonly "X-Amz-Date": string;
  readonly "x-amz-signature": string;
  readonly "x-amz-algorithm": string;
  readonly "x-amz-credential": string;
  readonly policy: string;

  readonly "x-amz-meta-filename": string;
  readonly "x-amz-meta-user-id": string;
  readonly key: string;
  readonly "Content-Type": string;
}

type UploadResult =
  | {
      readonly isSuccess: true;
      readonly filesToKeys: Readonly<Map<File, string>>;
    }
  | {
      readonly isSuccess: false;
      readonly error: Error;
    };

const MAX_WIDTH = 1400;
const TARGET_EXTENSION = "webp";
const TARGET_CONTENT_TYPE = `image/${TARGET_EXTENSION}`;

const compressImageFile = async (file: File): Promise<File> => {
  const originalBitmap = await createImageBitmap(file, {
    imageOrientation: "from-image",
  });

  const needsResize = originalBitmap.width > MAX_WIDTH;
  const resizedBitmap = needsResize
    ? await createImageBitmap(originalBitmap, {
        resizeWidth: MAX_WIDTH,
        resizeQuality: "high",
        imageOrientation: "from-image",
      })
    : originalBitmap;
  if (needsResize) originalBitmap.close();

  const canvas = new OffscreenCanvas(resizedBitmap.width, resizedBitmap.height);
  const bitmaprenderer = canvas.getContext("bitmaprenderer");

  if (bitmaprenderer) {
    bitmaprenderer.transferFromImageBitmap(resizedBitmap);
  } else {
    const ctx2d = canvas.getContext("2d");
    ctx2d?.drawImage(resizedBitmap, 0, 0);
    resizedBitmap.close();
  }

  const blob = await canvas.convertToBlob({
    type: TARGET_CONTENT_TYPE,
    quality: 0.8,
  });

  if (blob.type !== TARGET_CONTENT_TYPE) {
    throw new Error(`could not convert blob to ${TARGET_EXTENSION}`);
  }

  const trimmedFilename = file.name.trim();
  const lastDotIndex = trimmedFilename.lastIndexOf(".");
  const filenameWithoutExtension =
    lastDotIndex > 0
      ? trimmedFilename.substring(0, lastDotIndex)
      : trimmedFilename;
  const finalFilename = `${filenameWithoutExtension}.${TARGET_EXTENSION}`;

  return new File([blob], finalFilename, {
    type: TARGET_CONTENT_TYPE,
  });
};

const s3Client = axios.create({
  baseURL:
    typeof window === "undefined"
      ? serverEnv.S3_URL
      : getClientEnv("NEXT_PUBLIC_S3_URL"),
  timeout: 0,
});

const upload = async (
  files: File[],
  onProgress?: (percent: number) => void,
): Promise<UploadResult> => {
  if (files.length === 0) {
    return { isSuccess: true, filesToKeys: new Map() };
  }

  try {
    const compressedFiles = await Promise.all(
      files.map((f) => compressImageFile(f)),
    );

    const totalBytes = compressedFiles.reduce(
      (acc, file) => acc + file.size,
      0,
    );
    const uploadedBytesPerFile = new Array<number>(compressedFiles.length).fill(
      0,
    );
    onProgress?.(0);

    const uploadRequestDto: BatchUploadRequestDto = {
      files: compressedFiles.map((file) => ({ filename: file.name })),
    };
    const { data: uploadsMetadata } = await apiService.post<
      UploadRequestResponseDto[]
    >("/api/v1/upload/request", uploadRequestDto);

    if (uploadsMetadata.length !== compressedFiles.length) {
      return {
        isSuccess: false,
        error: new Error("mismatch of files and upload keys"),
      };
    }

    const uploadsFormData: FormData[] = uploadsMetadata.map(
      (uploadMetadata, index) => {
        const formData = new FormData();
        Object.entries(uploadMetadata).forEach(([key, value]) =>
          formData.append(key, value),
        );

        const file = compressedFiles[index] as File;
        formData.append("file", file);

        return formData;
      },
    );
    for (let i = 0; i < uploadsFormData.length; i++) {
      await s3Client.post("", uploadsFormData[i], {
        onUploadProgress(event) {
          if (!onProgress || !event.total) return;

          uploadedBytesPerFile[i] = event.loaded;
          const totalUploaded = uploadedBytesPerFile.reduce(
            (acc, uploadedBytes) => acc + uploadedBytes,
            0,
          );

          const percent = Math.round((totalUploaded * 100) / totalBytes);
          onProgress(percent);
        },
      });
    }

    const filesToKeys = new Map(
      files.map((file, index) => [file, uploadsMetadata[index]?.key as string]),
    );
    return { isSuccess: true, filesToKeys };
  } catch (e) {
    const error = e instanceof Error ? e : new Error("unknown s3 upload error");
    return { isSuccess: false, error };
  }
};

export const uploadService = { upload } as const;
