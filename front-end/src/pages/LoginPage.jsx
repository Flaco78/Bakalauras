import React, { useState } from 'react';
import { TextField, Container, Box, Typography } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import {useAuth} from "../context/AuthContext.jsx";
import ButtonFunky from "../components/ButtonFunky.jsx";

const LoginPage = () => {
    const { login } = useAuth();
    const [email, setEmail] = useState('');
    const [loginType, setLoginType] = useState('USER');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();
    const [errors, setErrors] = useState({});

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const endpoint = loginType === 'PROVIDER'
                ? '/api/auth/provider/login'
                : '/api/auth/login';
            console.log(loginType)
            const response = await axios.post(endpoint, { email, password });
            const token = response.data.token;
            login(token, loginType);
        } catch (error) {
            if (error.response && error.response.status === 401) {
                setErrors({ general: 'Neteisingas el. paštas arba slaptažodis.' });
            } else if (error.response && error.response.status === 404) {
                setErrors({ general: 'Tokia paskyra neegzistuoja.' });
            } else {
                setErrors({ general: 'Įvyko klaida. Bandykite dar kartą vėliau.' });
            }
        }
    };

    const goToSignup = () => {
        navigate('/register');
    };

    return (
        <Container component="main" maxWidth="xs">
            <Box sx={{display: 'flex', flexDirection: 'column', alignItems: 'center', mt: 5}}>
                <Typography variant="h3" sx={{ fontFamily: '"Comic Sans MS", cursive, sans-serif', fontWeight: 'bold', color: 'rgba(0,0,0,0.87)' }}>
                    PRISIJUNKITE
                </Typography>
                <Box sx={{ width: '100%', display: 'flex', justifyContent: 'center', mb: 2 }}>
                    <img
                        src="/Underline_02.png"
                        alt="underline"
                        style={{ width: '70%' }}
                    />
                </Box>
                {Object.keys(errors).length > 0 && (
                    <Box sx={{ backgroundColor: '#ffe0e0', padding: 2, borderRadius: 2, mb: 2 }}>
                        {errors.general && (
                            <Typography sx={{ color: 'red', fontWeight: 500 }}>
                                {errors.general}
                            </Typography>
                        )}
                        {Object.entries(errors).map(([field, message]) => {
                            if (field === 'general') return null;
                            return (
                                <Typography key={field} sx={{ color: 'red', fontSize: '14px' }}>
                                    {message}
                                </Typography>
                            );
                        })}
                    </Box>
                )}
                <form onSubmit={handleLogin} style={{width: '100%'}}>
                    <Typography variant="h5" sx={{textAlign: 'left', mb: 1}}>El. paštas</Typography>
                    <TextField
                        value={email}
                        onChange={(e) => {
                            setEmail(e.target.value);
                            setErrors({});
                        }}
                        fullWidth
                    />
                    <Typography variant="h5" sx={{textAlign: 'left', mb: 1, mt: 2}}>Slaptažodis</Typography>
                    <TextField
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        fullWidth
                    />
                    <Box sx={{ display: 'flex', justifyContent: 'center', mb: 2, gap: 2, mt: 2 }}>
                        <ButtonFunky
                            sx={{
                                backgroundColor: loginType === 'USER' ? '#81c784' : '#ef9a9a',
                                border: '2px solid #422800',
                                fontWeight: 'bold',
                            }}
                            onClick={() => setLoginType('USER')}
                        >
                            Vartotojas
                        </ButtonFunky>
                        <ButtonFunky
                            sx={{
                                backgroundColor: loginType === 'PROVIDER' ? '#81c784' : '#ef9a9a',
                                border: '2px solid #422800',
                                fontWeight: 'bold',
                            }}
                            onClick={() => setLoginType('PROVIDER')}
                        >
                            Tiekėjas
                        </ButtonFunky>
                    </Box>
                    <ButtonFunky fullWidth sx={{mt: 2}} onClick={handleLogin}>
                        Prisijungti
                    </ButtonFunky>
                </form>
                <Typography variant="h5" sx={{textAlign: 'center', mt: 5}}>Neturite paskyros?</Typography>
                <ButtonFunky
                    onClick={goToSignup} sx={{mt: 1}}
                >
                    Susikūrkite paspaudę čia
                </ButtonFunky>
            </Box>
        </Container>
    );
};

export default LoginPage;