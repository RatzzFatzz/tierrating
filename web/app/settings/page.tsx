import ChangePassword from "@/app/settings/change-password";
import {Card, CardContent, CardHeader} from "@/components/ui/card";

export default function Settings() {
    return (
        <div className={"flex flex-row justify-center"}>
            <Card>
                <CardHeader>
                    Change Password
                </CardHeader>
                <CardContent>
                    <ChangePassword />
                </CardContent>
            </Card>
        </div>
    )
}