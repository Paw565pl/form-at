import { getClientEnv } from "@/core/lib/env/client-env";
import { serverEnv } from "@/core/lib/env/server-env";
import { apiService } from "@/core/services/api-service";
import axios from "axios";

interface BatchUploadRequestDto {
  readonly files: {
    readonly filename: string;
  }[];
}

interface UploadPayloadDto {
  readonly "X-Amz-Date": string;
  readonly "x-amz-signature": string;
  readonly "x-amz-algorithm": string;
  readonly "x-amz-credential": string;
  readonly policy: string;

  readonly "x-amz-meta-filename": string;
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

class UploadService {
  private readonly TARGET_CONTENT_TYPE = "image/avif";
  private readonly s3Client = axios.create({
    baseURL:
      typeof window === "undefined"
        ? serverEnv.S3_URL
        : getClientEnv("NEXT_PUBLIC_S3_URL"),
    timeout: 0,
  });

  public async upload(
    files: File[],
    onProgress?: (percent: number) => void,
  ): Promise<UploadResult> {
    const compressedFiles = await Promise.all(
      files.filter((f) => f.size !== 0).map((f) => this.compressImageFile(f)),
    );
    if (compressedFiles.length === 0) {
      return { isSuccess: true, filesToKeys: new Map() };
    }

    const totalBytes = compressedFiles.reduce((acc, f) => acc + f.size, 0);
    const uploadedBytesPerFile = new Array<number>(compressedFiles.length).fill(
      0,
    );
    onProgress?.(0);

    try {
      const uploadRequestDto: BatchUploadRequestDto = {
        files: compressedFiles.map((f) => ({ filename: f.name })),
      };
      const { data: uploadsMetadata } = await apiService.post<
        UploadPayloadDto[]
      >("/api/v1/upload/request", uploadRequestDto);

      if (uploadsMetadata.length !== compressedFiles.length) {
        return {
          isSuccess: false,
          error: new Error("mismatch of files and upload keys"),
        };
      }

      const uploadsFormData: FormData[] = uploadsMetadata
        .map((uploadMetadata, index) => {
          const formData = new FormData();
          Object.entries(uploadMetadata).forEach(([key, value]) =>
            formData.append(key, value),
          );

          const file = compressedFiles[index];
          if (!file) return formData;

          formData.append("file", file);
          return formData;
        })
        .filter((f) => f.has("file"));
      for (let i = 0; i < uploadsFormData.length; i++) {
        await this.s3Client.post("", uploadsFormData[i], {
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
        compressedFiles.map((file, index) => [
          file,
          uploadsMetadata[index].key,
        ]),
      );
      return { isSuccess: true, filesToKeys };
    } catch (e) {
      const error = Error.isError(e) ? e : new Error("unknown s3 upload error");
      return { isSuccess: false, error };
    }
  }

  private async compressImageFile(file: File): Promise<File> {
    const src = URL.createObjectURL(file);

    try {
      const image = new Image();
      image.src = src;

      await new Promise((resolve, reject) => {
        image.onload = () => resolve(null);
        image.onerror = (e) => reject(e);
      });

      const width = Math.min(image.width, 1400);
      const resizedBitmap = await createImageBitmap(image, {
        resizeWidth: width,
      });
      const canvas = new OffscreenCanvas(
        resizedBitmap.width,
        resizedBitmap.height,
      );

      const bitmaprenderer = canvas.getContext("bitmaprenderer");
      if (bitmaprenderer) {
        bitmaprenderer.transferFromImageBitmap(resizedBitmap);
      } else {
        const ctx2d = canvas.getContext("2d");
        ctx2d?.drawImage(resizedBitmap, 0, 0);
      }

      const blob = await canvas.convertToBlob({
        type: this.TARGET_CONTENT_TYPE,
        quality: 0.8,
      });

      return new File([blob], file.name, { type: this.TARGET_CONTENT_TYPE });
    } finally {
      URL.revokeObjectURL(src);
    }
  }
}

export const uploadService = new UploadService();
