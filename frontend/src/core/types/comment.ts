export enum CommentRatingType {
  upvote = "UPVOTE",
  downvote = "DOWNVOTE",
}

export interface CommentResponseDto {
  readonly id: string;
  readonly authorName: string;
  readonly content: string;
  readonly ratingScore: number;
  readonly userRating: CommentRatingType | null;
  readonly createdAt: string;
  readonly updatedAt: string;
}

export interface CommentRequestDto {
  readonly content: string;
}

export interface CommentRatingResponseDto {
  readonly id: string;
  readonly type: CommentRatingType;
  readonly createdAt: string;
  readonly updatedAt: string;
}

export interface CommentRatingRequestDto {
  readonly type: CommentRatingType;
}
