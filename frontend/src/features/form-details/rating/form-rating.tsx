import { ICONS } from "@/core/config/icons";
import { cn } from "@/core/lib/cn";
import { useState } from "react";

const StarButton = ({
  fillFraction,
  isHovered,
  onMouseEnter,
  onMouseLeave,
  onClick,
}: {
  fillFraction: number;
  isHovered: boolean;
  onMouseEnter: () => void;
  onMouseLeave: () => void;
  onClick: () => void;
}) => {
  return (
    <button
      className="relative flex cursor-pointer items-center justify-center transition-transform hover:scale-110"
      onMouseEnter={onMouseEnter}
      onMouseLeave={onMouseLeave}
      onClick={onClick}
    >
      <div className="relative inline-block">
        {/* Background (empty) star */}
        <ICONS.star
          className={`transition-opacity ${
            isHovered ? "text-primary opacity-100" : "opacity-30"
          }`}
        />

        {/* Filled star overlay */}
        {(fillFraction > 0 || isHovered) && (
          <div
            className={cn(
              "absolute top-0 left-0 overflow-hidden transition-all",
              isHovered ? "text-primary" : null,
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

export const FormRating = () => {
  const [rating, setRating] = useState(4.3);
  const [hoveredIndex, setHoveredIndex] = useState<number | null>(null);

  const handleStarClick = (index: number) => {
    setRating(index + 1);
  };

  const handleStarHover = (index: number) => {
    setHoveredIndex(index);
  };

  const handleMouseLeave = () => {
    setHoveredIndex(null);
  };

  return (
    <section className="flex gap-3">
      <div className="flex">
        {[0, 1, 2, 3, 4].map((index) => {
          const fillFraction =
            hoveredIndex !== null
              ? 0
              : Math.round(Math.min(Math.max(rating - index, 0), 1) * 10) / 10;
          const isHovered = hoveredIndex !== null && index <= hoveredIndex;

          return (
            <StarButton
              key={index}
              fillFraction={fillFraction}
              isHovered={isHovered}
              onMouseEnter={() => handleStarHover(index)}
              onMouseLeave={handleMouseLeave}
              onClick={() => handleStarClick(index)}
            />
          );
        })}
      </div>
      <p>Rating: {rating}</p>
    </section>
  );
};
