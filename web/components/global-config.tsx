import getConfig from "next/config";

const runtimeConfig = getConfig() || {};
const { serverRuntimeConfig = {}, publicRuntimeConfig = {} } = runtimeConfig;

export const API_URL = serverRuntimeConfig.API_URL ||
    publicRuntimeConfig.API_URL ||
    process.env.API_URL ||
    'http://localhost:8080';