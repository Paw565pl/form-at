import { ICONS } from "@/core/config/icons";

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
        <ICONS.rate className="opacity-30 transition-opacity" />

        {/* User Filled Star */}
        {userRating && fillFraction === 1 && (
          <div className="text-primary absolute top-0 left-0 overflow-hidden transition-all">
            <ICONS.rate fill="var(--primary)" />
          </div>
        )}

        {/* Filled star overlay */}
        {(fillFraction > 0 || isHovered) && !userRating && (
          <div
            className="absolute top-0 left-0 overflow-hidden transition-all"
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
