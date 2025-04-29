import MainPage from "../pages/MainPage";
import AdminDashboard from "../pages/AdminDashboard.jsx";
import ProviderDashboard from "../pages/ProviderDashboard.jsx";

const routeConfig = [
    { path: "/main", component: MainPage, roles: null },
    { path: "/admin", component: AdminDashboard, roles: ["ADMIN"] },
    { path: "/provider", component: ProviderDashboard, roles: ["PROVIDER"] },
];
export default routeConfig;