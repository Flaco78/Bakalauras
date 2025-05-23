import React, {createContext, useContext, useState, useEffect, useCallback} from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [token, setToken] = useState(null);
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [loading, setLoading] = useState(true);
    const [user, setUser] = useState(null);
    const [roles, setRoles] = useState([]);
    const navigate = useNavigate();

    const fetchUserDetails = useCallback(async (token) => {
        try {
            const response = await axios.get("/api/auth/user", {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            setUser(response.data);
            setRoles(response.data.roles);
            localStorage.setItem('user', JSON.stringify(response.data));
        } catch (error) {
            console.error("Error fetching user roles:", error);
            localStorage.removeItem('token');
            setToken(null);
            setIsAuthenticated(false);
            setUser(null);
            setRoles([]);
            navigate('/login');
        }
    }, [navigate]);
    const fetchProviderDetails = useCallback(async (token) => {
        try {
            const response = await axios.get("/api/auth/provider", {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            setUser(response.data);
            setRoles(response.data.roles || [{ name: 'PROVIDER' }]); // jei nėra roles lauke
            localStorage.setItem('user', JSON.stringify(response.data));
        } catch (error) {
            console.error("Error fetching provider details:", error);
            localStorage.removeItem('token');
            setToken(null);
            setIsAuthenticated(false);
            setUser(null);
            setRoles([]);
            navigate('/login');
        }
    }, [navigate]);

    useEffect(() => {
        const storedToken = localStorage.getItem('token');
        const cachedUser = localStorage.getItem('user');

        localStorage.getItem('roles');
        const initializeAuthentication = async () => {
            if (storedToken) {
                setToken(storedToken);
                setIsAuthenticated(true);

                if (cachedUser) {
                    const parsedUser = JSON.parse(cachedUser);
                    setUser(parsedUser);
                    setRoles(parsedUser.roles);
                } else {
                    const loginType = localStorage.getItem('loginType') || 'USER';
                    if (loginType === 'PROVIDER') {
                        await fetchProviderDetails(storedToken);
                    } else {
                        await fetchUserDetails(storedToken);
                    }
                }
            }
            setLoading(false);
        };


        initializeAuthentication().catch((error) => {
            console.error("Error during authentication initialization:", error);
            setLoading(false);
        })
    }, [fetchUserDetails]);


    const login = (newToken, loginType = 'USER') => {
        localStorage.setItem('token', newToken);
        localStorage.setItem('loginType', loginType);
        setToken(newToken);
        setIsAuthenticated(true);

        if (loginType === 'PROVIDER') {
            fetchProviderDetails(newToken);
        } else {
            fetchUserDetails(newToken);
        }

        navigate('/main');
    };

    const logout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        localStorage.removeItem('selectedChildId');
        localStorage.removeItem('loginType');
        setToken(null);
        setIsAuthenticated(false);
        setUser(null);
        setRoles([]);
        navigate('/');
    };

    return (
        <AuthContext.Provider value={{ token, isAuthenticated, loading, user, roles, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    return useContext(AuthContext);
};

export default AuthContext;