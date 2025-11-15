export class SortOptionsDto {
  constructor(
    public field: string,
    public order: "asc" | "desc",
  ) {
    this.field = field;
    this.order = order;
  }

  public getSearchParamValue(): string {
    return `${this.field},${this.order}`;
  }
}
