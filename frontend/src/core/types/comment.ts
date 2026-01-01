export interface CommentResponseDto {
  readonly id: string;
  readonly authorName: string;
  readonly content: string;
  readonly ratingScore: number | null;
  readonly userRating: "UPVOTE" | "DOWNVOTE" | null;
  readonly createdAt: string;
  readonly updatedAt: string;
}
