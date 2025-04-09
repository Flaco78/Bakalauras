import './App.css'
import {BrowserRouter as Router, Routes, Route, Navigate} from "react-router-dom";
import {AuthProvider, useAuth} from "./context/AuthContext.jsx";
import axios from "axios";
import LoginPage from "./pages/LoginPage.jsx";
import MainPage from "./pages/MainPage.jsx";
import RegistrationPage from "./pages/RegistrationPage.jsx";
import routeConfig from "./config/routeConfig.jsx";
import Navbar from "./components/Navbar.jsx";
import LandingPage from "./pages/LandingPage.jsx";
import ProviderRegistrationPage from "./pages/ProviderRegistrationPage.jsx";
import UserProfile from "./pages/UserProfile.jsx";

axios.defaults.baseURL = 'http://localhost:8080';

function App() {
    return (
    <Router>
        <AuthProvider>
                <Navbar />
                <Routes>
                    {routeConfig.map(({ path, component, roles }) => (
                        <Route
                            key={path}
                            path={path}
                            element={<ProtectedRoute component={component} roles={roles} />}
                        />
                    ))}
                    <Route path="/" element={<LandingPage />} />
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/main" element={<ProtectedRoute component={MainPage} />} />
                    <Route path="/register" element={<RegistrationPage />} />
                    <Route path="/register-provider" element={<ProviderRegistrationPage />} />
                    <Route path="/user-profile" element={<UserProfile />} />
                </Routes>
        </AuthProvider>
    </Router>
    );
}

function ProtectedRoute({ component: Component, roles: allowedRoles }) {
    const { isAuthenticated, roles, loading } = useAuth();

    if (loading) {
        return <div>Kraunama...</div>;
    }

    const hasAccess = !allowedRoles || allowedRoles.some(role => roles.includes(role));

    if (!isAuthenticated) {
        return <Navigate to="/" replace />;
    }

    return hasAccess ? <Component /> : <Navigate to="/unauthorized" replace />;
}

export default App;
