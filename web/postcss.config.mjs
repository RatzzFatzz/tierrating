import {plugin} from "next/dist/build/webpack/config/helpers";

const config = {
  plugins: ["@tailwindcss/postcss",
            plugin(function ({ matchUtilities, theme }) {
              matchUtilities(
                  {
                    'auto-fill': (value) => ({
                      gridTemplateColumns: `repeat(auto-fill, minmax(min(${value}, 100%), 1fr))`,
                    }),
                    'auto-fit': (value) => ({
                      gridTemplateColumns: `repeat(auto-fit, minmax(min(${value}, 100%), 1fr))`,
                    }),
                  },
                  {
                    values: theme('width', {}),
                  }
              )
            })]
};

export default config;
