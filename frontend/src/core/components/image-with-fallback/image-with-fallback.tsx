import Image, { ImageProps } from "next/image";
import { ReactNode } from "react";

export interface ImageWithFallbackProps extends Omit<ImageProps, "src"> {
  readonly src: string | null;
  readonly fallback: ReactNode;
}

export const ImageWithFallback = ({
  src,
  fallback,
  ...props
}: ImageWithFallbackProps) => {
  if (!src) return fallback;

  // alt comes from component consumer
  // eslint-disable-next-line jsx-a11y/alt-text
  return <Image src={src} {...props} />;
};
