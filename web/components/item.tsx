import Image from "next/image"

interface ItemProps {
    id: string
    title: string
    cover: string
}

export default function Item({ id, title, cover }: ItemProps) {
    return (
        <div className="flex flex-col items-center bg-white rounded-md shadow-sm p-2 w-24 h-40 cursor-move hover:shadow-md transition-shadow">
            <div className="relative w-full h-full mb-1">
                <Image src={cover || "/placeholder.svg"} alt={title} fill className="object-cover rounded-md" />
            </div>
            <span className="text-xs text-center line-clamp-2 w-full h-12">{title}</span>
        </div>
    )
}
