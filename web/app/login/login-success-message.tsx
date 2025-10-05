"use client"

import {useSearchParams} from "next/navigation";

export function SuccessMessage() {
    const searchParams = useSearchParams()
    const signupSuccess = searchParams.get('signup') === 'success'

    return (
        <div>
            {signupSuccess && (
                <div className="bg-green-500/15 text-green-500 text-sm p-3 rounded-md">
                    Account created successfully! You can now log in.
                </div>
            )}
        </div>
    )
}