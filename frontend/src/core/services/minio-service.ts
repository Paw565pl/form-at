import { getClientEnv } from "@/core/lib/env/client-env";
import { serverEnv } from "@/core/lib/env/server-env";
import { authenticatedApiService } from "@/core/services/api-service";
import axios from "axios";

interface FileUploadRequestDto {
  readonly files: {
    readonly fileName: string;
  }[];
}

interface UploadMetadata {
  readonly filename: string;
  readonly "x-amz-date": string;
  readonly "x-amz-signature": string;
  readonly "x-amz-algorithm": string;
  readonly key: string;
  readonly "x-amz-credential": string;
  readonly policy: string;
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

class MinioService {
  private minioClient = axios.create({
    baseURL:
      typeof window === "undefined"
        ? serverEnv.MINIO_URL
        : getClientEnv("NEXT_PUBLIC_MINIO_URL"),
    adapter: "fetch",
  });

  public async upload(files: File[]): Promise<UploadResult> {
    const nonEmptyFiles = files.filter((f) => f.size !== 0);
    if (nonEmptyFiles.length === 0)
      return { isSuccess: true, filesToKeys: new Map() };

    try {
      const uploadRequestDto: FileUploadRequestDto = {
        files: nonEmptyFiles.map((f) => ({ fileName: f.name })),
      };
      const { data: uploadsMetadata } = await authenticatedApiService.post<
        UploadMetadata[]
      >("/api/v1/upload/request", uploadRequestDto);

      if (uploadsMetadata.length !== nonEmptyFiles.length)
        return {
          isSuccess: false,
          error: new Error("mismatch of files and upload keys"),
        };

      const uploadsFormData: FormData[] = uploadsMetadata
        .map((uploadMetadata, index) => {
          const formData = new FormData();
          Object.entries(uploadMetadata).forEach(([key, value]) =>
            formData.append(key, value),
          );

          const file = nonEmptyFiles[index];
          if (!file) return formData;

          formData.append("file", file);
          return formData;
        })
        .filter((f) => f.has("file"));
      for (const uploadFormData of uploadsFormData) {
        await this.minioClient.post("", uploadFormData);
      }

      const filesToKeys = new Map(
        nonEmptyFiles.map((file, index) => [file, uploadsMetadata[index].key]),
      );
      return { isSuccess: true, filesToKeys };
    } catch (e) {
      const error = Error.isError(e)
        ? e
        : new Error("unknown minio upload error");
      return { isSuccess: false, error };
    }
  }
}

export const minioService = new MinioService();
