import {
  ImageWithFallback,
  ImageWithFallbackProps,
} from "@/core/components/image-with-fallback/image-with-fallback";
import { FormImagePlaceholder } from "@/core/form-image/form-image-placeholder";

export const FormImageWithFallback = ({
  src,
  ...props
}: Omit<ImageWithFallbackProps, "fallback">) => {
  return (
    <ImageWithFallback
      src={src}
      fallback={<FormImagePlaceholder />}
      {...props}
    />
  );
};
