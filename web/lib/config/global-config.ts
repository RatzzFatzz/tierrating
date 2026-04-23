import { getPublicEnv } from "@/lib/public-env";

export const API_URL = getPublicEnv().API_URL || "http://localhost:8080";

export const REDIRECT_URL_PLACEHOLDER: string = "REDIRECT_URL_PLACEHOLDER";
export const CLIENT_ID_PLACEHOLDER: string = "CLIENT_ID_PLACEHOLDER";
