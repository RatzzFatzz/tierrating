import { createPublicEnv } from "next-public-env";

export const { getPublicEnv, PublicEnv } = createPublicEnv({
	API_URL: process.env.API_URL,
});
