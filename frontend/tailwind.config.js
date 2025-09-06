/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        coffee: {
          50: '#fdf7f0',
          100: '#fbeee0',
          200: '#f6d9bd',
          300: '#f0c094',
          400: '#e8a067',
          500: '#e18a47',
          600: '#d3713c',
          700: '#af5a34',
          800: '#8c4a31',
          900: '#723e2a',
        },
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
      },
    },
  },
  plugins: [
    require('@tailwindcss/forms'),
  ],
}
