import { ICONS } from "@/core/config/icons";
import { cn } from "@/core/lib/cn";

interface StarButtonProps {
  readonly fillFraction: number;
  readonly userRating: number | null;
  readonly isHovered: boolean;
  readonly onMouseEnter: () => void;
  readonly onMouseLeave: () => void;
  readonly onClick: () => void;
  readonly disabled?: boolean;
}

export const StarButton = ({
  userRating,
  fillFraction,
  isHovered,
  onMouseEnter,
  onMouseLeave,
  onClick,
  disabled = false,
}: StarButtonProps) => {
  return (
    <button
      className="relative flex cursor-pointer items-center justify-center transition-transform hover:scale-110 disabled:cursor-not-allowed disabled:opacity-50"
      onMouseEnter={onMouseEnter}
      onMouseLeave={onMouseLeave}
      onClick={onClick}
      disabled={disabled}
    >
      <div className="relative inline-block">
        {/* Background (empty) star */}
        <ICONS.star
          className={cn(
            "transition-opacity",
            isHovered ? "text-primary opacity-100" : "opacity-30",
          )}
        />

        {/* Filled star overlay */}
        {(fillFraction > 0 || isHovered) && (
          <div
            className={cn(
              "absolute top-0 left-0 overflow-hidden transition-all",
              isHovered || (userRating !== null && fillFraction === 1)
                ? "text-primary"
                : null,
            )}
            style={{
              width: isHovered ? "100%" : `${fillFraction * 100}%`,
            }}
          >
            <ICONS.star />
          </div>
        )}
      </div>
    </button>
  );
};
