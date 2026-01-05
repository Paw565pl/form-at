export enum CommentRatingType {
  upvote = "UPVOTE",
  downvote = "DOWNVOTE",
}

export interface CommentResponseDto {
  readonly id: string;
  readonly authorName: string;
  readonly content: string;
  readonly ratingScore: number | null;
  readonly userRating: CommentRatingType | null;
  readonly createdAt: Date;
  readonly updatedAt: Date;
}

export interface CommentRequestDto {
  readonly content: string;
}

export interface CommentRatingResponseDto {
  readonly id: string;
  readonly type: CommentRatingType;
  readonly createdAt: Date;
  readonly updatedAt: Date;
}

export interface CommentRatingRequestDto {
  readonly type: CommentRatingType;
}
