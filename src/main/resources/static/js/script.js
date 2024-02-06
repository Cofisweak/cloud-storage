//Theme Switcher
const themeSwitcher = document.getElementById("theme-switcher");
const getStoredTheme = () => localStorage.getItem("theme");
const setStoredTheme = (theme) => localStorage.setItem("theme", theme);
const getCurrentTheme = () => document.body.getAttribute("data-bs-theme");

const setLightTheme = () => {
  document.body.setAttribute("data-bs-theme", "light");
};

const setDarkTheme = () => {
  document.body.setAttribute("data-bs-theme", "dark");
};

const setSunIcon = () => {
  themeSwitcher.setAttribute("src", "/images/sun.svg");
};

const setMoonIcon = () => {
  themeSwitcher.setAttribute("src", "/images/moon.svg");
};

const updateTheme = (theme) => {
  switch (theme) {
    case "dark":
      setDarkTheme();
      setStoredTheme("dark");
      setSunIcon();
      break;
    default:
      setLightTheme();
      setStoredTheme("light");
      setMoonIcon();
  }
};

const themeSwitcherCallback = () => {
  const currentTheme = getCurrentTheme();
  switch (currentTheme) {
    case "light":
      updateTheme("dark");
      break;
    default:
      updateTheme("light");
  }
};
window.addEventListener("DOMContentLoaded", () => {
  const storedTheme = getStoredTheme();
  if (storedTheme) {
    updateTheme(storedTheme);
  } else {
    setLightTheme();
  }
});

themeSwitcher.addEventListener("click", themeSwitcherCallback);