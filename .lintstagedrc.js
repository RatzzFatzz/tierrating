module.exports = {
  "**/*.java": (filenames) => {
    const javaFiles = filenames.filter((f) => f.startsWith("api/"));
    if (javaFiles.length === 0) return [];
    return [`mvn checkstyle:check --file api/pom.xml`];
  },
  "web/**/*.{js,jsx,ts,tsx}": (filenames) => {
    const cwd = process.cwd();
    return [
      `cd web && npx eslint --fix ${filenames.map((f) => f.replace("web/", "")).join(" ")}`,
      `cd web && npx prettier --write ${filenames.map((f) => f.replace("web/", "")).join(" ")}`,
    ];
  },
  "web/**/*.{json,css,md}": (filenames) => {
    const cwd = process.cwd();
    return [`cd web && npx prettier --write ${filenames.map((f) => f.replace("web/", "")).join(" ")}`];
  },
};
