import Image from "next/image";

export default function TmdbDisclaimer() {
    return (
        <div className={"flex gap-1 justify-center"}>
            <Image src={"/tmdb.svg"} alt={"tmdb logo"} width={26} height={26}/>
            Images provided by
            <a href={"https://themoviedb.org"}>themoviedb.org</a>
        </div>
    )
}