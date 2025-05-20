import Image from "next/image"

import { AspectRatio } from "@/components/ui/aspect-ratio"

interface RatingItem {
  id: number;
  title: {
    romaji: string;
    english: string;
  };
  coverImage: {
    large: string;
    extraLarge: string;
  };
}

async function getData(): Promise<RatingItem[]> {
  const result = await fetch("http://localhost:8080/data/anilist/manga");
  return result.json();
}

export default async function Profile() {
  const items = await getData();

  return (
    <main className="justify-center">
      <div className="grid grid-cols-6 gap-4">
        {items.map(item => (
          <div key={item.id}>
            <AspectRatio ratio={9/16}>
              <Image fill={true} src={item.coverImage.large} alt="Image" className="rounded-md object-cover" />
            </AspectRatio>
            <p>{item.title.english}</p>
          </div>
        ))}
      </div>
    </main>
  );
}
