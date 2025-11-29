import { Badge } from "@/core/components/ui/badge";
import type { User } from "@/features/auth/types/user";
import { forms } from "@/features/form-list/example-forms";
import { UserForm } from "@/features/user/components/user-form";
import { PartyPopper } from "lucide-react";
import Image from "next/image";
import { HistoryItem } from "./components/history-item";

const User = {
  id: "1",
  name: "John Doe",
  email: "john.doe@example.com",
  image:
    "https://www.svgrepo.com/show/382109/male-avatar-boy-face-man-user-7.svg",
  roles: [],
  achievements: [
    { id: "1", name: "Form Creator", description: "Created 5 forms" },
    {
      id: "2",
      name: "Submission Master",
      description: "Received 100 submissions",
    },
  ],
};

const userForms = forms.slice(0, 3);

export const UserProfilePage = () => {
  return (
    <main className="px-5 py-10 md:px-0">
      <div className="flex flex-col gap-6 pb-6 md:flex-row">
        <section className="flex-1">
          <header className="flex items-center gap-2 py-2">
            {User.image && (
              <Image
                className="rounded-full border-2 border-white"
                src={User.image}
                alt={User.name}
                width={100}
                height={100}
              />
            )}
            <span>
              <h1 className="pb-2 text-4xl font-semibold">{User.name}</h1>
              <h2 className="pb-2">{User.email}</h2>
            </span>
          </header>
          <div className="flex gap-1 pb-2">
            <Badge>
              <PartyPopper />
              5+ forms created
            </Badge>
            <Badge variant="secondary" className="text-white">
              <PartyPopper />
              100+ submissions received
            </Badge>
          </div>
          <section className="bg-accent rounded-md p-3 shadow-md">
            <p>
              Lorem ipsum dolor sit amet consectetur adipisicing elit. Minus
              tempora ab voluptatibus. Reprehenderit quae delectus repellendus
              quia culpa. Distinctio quae dolor aut, autem error veniam maiores
              consequatur eum veritatis temporibus. Rem suscipit atque maxime
              aspernatur ipsa repudiandae? Ducimus maxime quam sapiente ratione
              temporibus numquam corrupti quis cumque maiores! Aspernatur ipsum
              tenetur quia id, ea blanditiis quas, voluptas repellendus tempora
              sunt a! Nesciunt error consequatur voluptas, autem dolore unde
              obcaecati, ea, fugiat laborum veritatis excepturi quas dignissimos
              eligendi illum. Corrupti distinctio facilis nesciunt sint quae
              cupiditate, cumque eveniet nisi placeat veritatis odio explicabo,
              consequuntur maxime totam. Adipisci corrupti doloribus atque
              explicabo.
            </p>
          </section>
        </section>

        <section className="bg-accent flex w-full max-w-2xl flex-1 flex-col gap-2 rounded-md p-3 shadow-sm">
          <h2 className="text-secondary-foreground font-semibold">
            {User.name}'s Forms:
          </h2>
          {userForms.map((form) => (
            <UserForm key={form.id} form={form} />
          ))}
        </section>
      </div>

      <section className="bg-accent flex flex-col gap-2 rounded-md p-3 shadow-sm">
        <h3 className="text-secondary-foreground font-semibold">
          {User.name}'s History:
        </h3>
        <HistoryItem
          content="User John Doe has made submission to form: "
          formName="Quiz o kotach"
          date="12.03.2024"
          badgeVariant="secondary"
        />
        <HistoryItem
          content="User John Doe has created a new public form: "
          formName="Quiz o kotach"
          date="12.03.2024"
          badgeVariant="default"
        />
        <HistoryItem
          content="User John Doe has achived a perfect score in form: "
          formName="Quiz o kotach"
          date="12.03.2024"
          badgeVariant="secondary"
        />
      </section>
    </main>
  );
};
