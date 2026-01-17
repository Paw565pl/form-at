export const getStarFillFraction = (
  starIndex: number,
  hoveredIndex: number | null,
  userRating: number | null,
  ratingAvg: number,
): number => {
  // When hovering, fill all stars up to the hovered one
  if (hoveredIndex !== null) {
    return starIndex <= hoveredIndex ? 1 : 0;
  }

  // If user has rated, show their rating as filled stars
  if (userRating !== null) {
    return starIndex < userRating ? 1 : 0;
  }

  // Otherwise show the average rating with partial fills
  const fillAmount = ratingAvg - starIndex;
  if (fillAmount <= 0) {
    return 0;
  }
  if (fillAmount >= 1) {
    return 1;
  }
  return Math.round(fillAmount * 10) / 10;
};
