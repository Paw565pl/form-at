import { Badge } from "@/core/components/ui/badge";
import { PartyPopper } from "lucide-react";
import Image from "next/image";
import type { User } from "../auth/types/user";
import { forms } from "../form-list/example-forms";
import { UserForm } from "./components/user-form";

const User: User = {
  id: "1",
  name: "John Doe",
  email: "john.doe@example.com",
  image:
    "https://www.svgrepo.com/show/382109/male-avatar-boy-face-man-user-7.svg",
  roles: [],
};

const userForms = forms.slice(0, 3);

export const UserProfilePage = () => {
  return (
    <main className="py-10">
      <div className="flex gap-6">
        <section>
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
              <h1 className="pb-2 text-4xl">{User.name}</h1>
              <div className="flex gap-1">
                <Badge>
                  <PartyPopper />
                  5+ forms created
                </Badge>
                <Badge variant="secondary">
                  <PartyPopper />
                  100+ submissions received
                </Badge>
              </div>
            </span>
          </header>
          <p className="rounded-md border p-2">
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

        <section className="flex w-2xl shrink-0 flex-col gap-1">
          <h2>User Forms:</h2>
          {userForms.map((form) => (
            <UserForm key={form.id} form={form} />
          ))}
        </section>
      </div>

      <section className="flex flex-col gap-2">
        <h3>History</h3>
        <span className="bg-card flex justify-between rounded-md border p-2">
          <div className="flex items-center gap-2">
            User John Doe has made submission to form:
            <Badge variant={"secondary"}>Quiz o kotach</Badge>
          </div>
          <Badge variant={"outline"}>12.03.2024</Badge>
        </span>
        <span className="bg-card flex justify-between rounded-md border p-2">
          <div className="flex items-center gap-2">
            User John Doe has created a new public form:
            <Badge>Quiz o kotach</Badge>
          </div>
          <Badge variant={"outline"}>12.03.2024</Badge>
        </span>
        <span className="bg-card flex justify-between rounded-md border p-2">
          <div className="flex items-center gap-2">
            User John Doe has achived a perfect score in form:
            <Badge>Quiz o kotach</Badge>
          </div>
          <Badge variant={"outline"}>12.03.2024</Badge>
        </span>
      </section>
    </main>
  );
};
