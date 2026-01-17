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
  readonly ariaLabel?: string;
}

export const StarButton = ({
  userRating,
  fillFraction,
  isHovered,
  onMouseEnter,
  onMouseLeave,
  onClick,
  disabled = false,
  ariaLabel,
}: StarButtonProps) => {
  return (
    <button
      className="relative flex cursor-pointer items-center justify-center transition-transform hover:scale-110 disabled:cursor-not-allowed disabled:opacity-50"
      onMouseEnter={onMouseEnter}
      onMouseLeave={onMouseLeave}
      aria-label={ariaLabel}
      onClick={onClick}
      disabled={disabled}
    >
      <div className="relative inline-block">
        {/* Background (empty) star */}
        <ICONS.rate className="opacity-30 transition-opacity" />

        {/* User Filled Star */}
        {userRating && fillFraction === 1 && (
          <div
            className={cn(
              "text-primary absolute top-0 left-0 overflow-hidden transition-all",
              isHovered ? "brightness-175" : null,
            )}
          >
            <ICONS.rate className={isHovered ? "fill-none" : "fill-primary"} />
          </div>
        )}

        {/* Filled star overlay */}
        {(fillFraction > 0 || isHovered) && !userRating && (
          <div
            className={cn(
              "text-primary absolute top-0 left-0 overflow-hidden transition-all",
              isHovered ? "brightness-175" : null,
            )}
            style={{
              width: isHovered ? "100%" : `${fillFraction * 100}%`,
            }}
          >
            <ICONS.rate />
          </div>
        )}
      </div>
    </button>
  );
};
