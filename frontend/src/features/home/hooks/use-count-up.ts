import { useEffect, useState } from "react";

export const useCountUp = (target: number, maxDuration = 1000) => {
  const [value, setValue] = useState(0);

  useEffect(() => {
    const adjustedDuration = Math.max(150, Math.min(maxDuration, target * 40));
    const steps = Math.max(1, adjustedDuration / 16);
    const increment = target / steps;
    let start = 0;

    const interval = setInterval(() => {
      start += increment;
      if (start >= target) {
        setValue(target);
        clearInterval(interval);
      } else {
        setValue(Math.floor(start));
      }
    }, 16);
    return () => clearInterval(interval);
  }, [target, maxDuration]);
  return value;
};
