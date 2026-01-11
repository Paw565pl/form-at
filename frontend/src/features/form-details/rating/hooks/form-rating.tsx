import { ICONS } from "@/core/config/icons";
import { useState } from "react";

const StarButton = ({ fillFraction }: { fillFraction: number }) => {
  return (
    <button className="relative">
      <div className="relative inline-block">
        {/* Background (empty) star */}
        <ICONS.star className="opacity-30" />

        {/* Filled star overlay */}
        {fillFraction > 0 && (
          <div
            className="absolute top-0 left-0 overflow-hidden"
            style={{
              width: `${fillFraction * 100}%`,
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
  const [rating, setRating] = useState(2.1);

  const handleStarClick = (index: number, half: boolean) => {
    setRating(index + (half ? 0.5 : 1));
  };

  return (
    <section className="flex gap-3">
      <div className="flex gap-1">
        {[0, 1, 2, 3, 4].map((index) => {
          const fillFraction =
            Math.round(Math.min(Math.max(rating - index, 0), 1) * 10) / 10;

          return <StarButton key={index} fillFraction={fillFraction} />;
        })}
      </div>
      <p>Rating: {rating}</p>
    </section>
  );
};
