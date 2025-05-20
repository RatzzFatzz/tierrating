import {Button} from "@/components/ui/button";
import Link from "next/link";

export default function Profile() {
  return (
      <div>
        <Button asChild>
          <Link href="/profile/anilist/anime">AniList - Anime</Link>
        </Button>
        <Button asChild>
          <Link href="/profile/anilist/manga">AniList - Manga</Link>
        </Button>
      </div>
  );
}