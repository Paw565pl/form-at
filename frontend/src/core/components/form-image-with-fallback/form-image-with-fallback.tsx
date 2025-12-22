import { FormImagePlaceholder } from "@/core/components/form-image-with-fallback/form-image-placeholder";
import Image, { ImageProps } from "next/image";

interface FormImageWithFallbackProps extends Omit<ImageProps, "src"> {
  readonly src: string | null;
}

export const FormImageWithFallback = ({
  src,
  ...props
}: FormImageWithFallbackProps) => {
  if (!src) return <FormImagePlaceholder />;

  // alt comes from component consumer
  // eslint-disable-next-line jsx-a11y/alt-text
  return <Image src={src} {...props} />;
};
