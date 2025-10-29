"use client";

import { Button } from "@/core/components/ui/button";
import { Card } from "@/core/components/ui/card";
import { Input } from "@/core/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/core/components/ui/select";
import { FormatDate } from "@/features/public-forms-list-view/FormatDate";
import {
  forms,
  placeholder_image_url,
} from "@/features/public-forms-list-view/forms";
import { LayoutGrid, List } from "lucide-react";
import Link from "next/link";
import { useRef, useState } from "react";

export default function PublicFormsListView() {
  const loader = useRef<HTMLDivElement | null>(null);
  const [gridLayout, setGridLayout] = useState(false);

  return (
    <main
      id="PublicFormsListView"
      className="forms-list flex w-full flex-col gap-2 px-5 py-10 lg:px-30"
    >
      <header className="mb-2 flex flex-wrap items-center justify-between gap-4">
        <h1 className="ml-4 text-xl font-bold">Showing {forms.length} Forms</h1>
        <div id="FormFilterBar" className="flex flex-wrap gap-2 md:flex-nowrap">
          <form
            className="w-full max-w-70"
            onSubmit={(e) => {
              e.preventDefault();
              // To-do: Implement search functionality
            }}
          >
            <Input
              placeholder="Search forms..."
              type="text"
              className="md:min-w-60"
            />
          </form>

          <Select defaultValue={"newest"}>
            <SelectTrigger className="w-full max-w-70">
              Sort by
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="newest">Newest</SelectItem>
              <SelectItem value="oldest">Oldest</SelectItem>
              <SelectItem value="name">Name</SelectItem>
              <SelectItem value="questions">Questions</SelectItem>
              <SelectItem value="submissions">Submissions</SelectItem>
              <SelectItem value="duration">Duration</SelectItem>
            </SelectContent>
          </Select>

          <div className="flex gap-2">
            <Button
              size="icon"
              variant={gridLayout ? "outline" : "default"}
              onClick={() => setGridLayout(false)}
            >
              <List />
            </Button>
            <Button
              size="icon"
              variant={gridLayout ? "default" : "outline"}
              onClick={() => setGridLayout(true)}
            >
              <LayoutGrid />
            </Button>
          </div>
        </div>
      </header>

      {!gridLayout &&
        forms.map((form) => (
          <Link href={{ pathname: `/forms/${form.id}` }} key={form.id}>
            <Card
              key={form.title}
              className="hover:border-primary gap-1 p-3 transition-all"
            >
              <header className="flex flex-wrap items-center justify-between gap-1">
                <h1 className="font-medium">{form.title}</h1>
                <span className="text-muted-foreground text-sm">
                  by {form.author}
                </span>
              </header>
              <p className="text-sm lg:mr-36">{form.description}</p>
              <footer className="text-muted-foreground mt-1 flex flex-wrap justify-between text-sm">
                <span>
                  {form.questions} questions • {form.submissions} submissions •{" "}
                  {form.duration} min
                </span>
                <span className="text-muted-foreground text-sm">
                  {FormatDate(form.date_created, true)}
                </span>
              </footer>
            </Card>
          </Link>
        ))}

      {gridLayout && (
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {forms.map((form) => (
            <Link href={{ pathname: `/forms/${form.id}` }} key={form.id}>
              <Card
                key={form.title}
                className="group hover:border-primary h-full overflow-hidden transition-all"
              >
                <div
                  style={{
                    backgroundImage: `url(${form.image_url || placeholder_image_url})`,
                    backgroundSize: "cover",
                    backgroundPosition: "center",
                  }}
                  className="min-h-36 origin-bottom rounded-t-md transition-transform group-hover:scale-105"
                ></div>
                <div className="flex h-full flex-col gap-2 p-3">
                  <header className="bg-card flex flex-wrap items-center justify-between">
                    <h1 className="font-medium">{form.title}</h1>
                    <span className="text-muted-foreground text-sm">
                      by {form.author}
                    </span>
                  </header>
                  <p className="line-clamp-4 overflow-hidden text-sm">
                    {form.description}
                  </p>
                  <footer className="text-muted-foreground mt-auto flex flex-wrap justify-between gap-1 text-sm">
                    <span>
                      {form.questions} questions • {form.submissions}{" "}
                      submissions
                    </span>
                    <span className="text-muted-foreground text-sm">
                      {FormatDate(form.date_created, true)}
                    </span>
                  </footer>
                </div>
              </Card>
            </Link>
          ))}
        </div>
      )}

      <div ref={loader} className="h-px-40" />

      <p className="text-muted-foreground p-4 text-center text-sm">
        You have reached the end.
      </p>
    </main>
  );
}
