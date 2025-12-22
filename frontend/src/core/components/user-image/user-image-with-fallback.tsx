import { UserImagePlaceholder } from "@/core/components/user-image/user-image-placeholder";
import Image, { ImageProps } from "next/image";

interface UserImageWithFallbackProps extends Omit<ImageProps, "src"> {
  readonly src: string | null;
}

export const UserImageWithFallback = ({
  src,
  ...props
}: UserImageWithFallbackProps) => {
  if (!src) return <UserImagePlaceholder />;

  // alt comes from component consumer
  // eslint-disable-next-line jsx-a11y/alt-text
  return <Image src={src} {...props} />;
};
