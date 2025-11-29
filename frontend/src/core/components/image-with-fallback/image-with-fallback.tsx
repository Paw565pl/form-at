import { ImagePlaceholder } from "@/core/components/image-with-fallback/image-placeholder";
import Image, { ImageProps } from "next/image";

interface ImageWithFallbackProps extends Omit<ImageProps, "src"> {
  readonly src: string | null;
}

export const ImageWithFallback = ({
  src,
  ...props
}: ImageWithFallbackProps) => {
  if (!src) return <ImagePlaceholder />;

  // alt comes from component consumer
  // eslint-disable-next-line jsx-a11y/alt-text
  return <Image src={src} {...props} />;
};
