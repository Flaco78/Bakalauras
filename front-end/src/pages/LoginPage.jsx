import React, { useState } from 'react';
import { TextField, Container, Box, Typography } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import {useAuth} from "../context/AuthContext.jsx";
import ButtonFunky from "../components/ButtonFunky.jsx";

const LoginPage = () => {
    const { login } = useAuth();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('/api/auth/login', { email, password });
            const token = response.data.token;
            login(token);
        } catch (error) {
            console.error('Login error', error);
        }
    };

    const goToSignup = () => {
        navigate('/register');
    };

    return (
        <Container component="main" maxWidth="xs">
            <Box sx={{display: 'flex', flexDirection: 'column', alignItems: 'center', mt: 5}}>
                <Typography variant="h3" sx={{ fontFamily: '"Comic Sans MS", cursive, sans-serif', fontWeight: 'bold', color: 'rgba(0,0,0,0.87)' }}>
                    LOGIN
                </Typography>
                <Box sx={{ width: '100%', display: 'flex', justifyContent: 'center', mb: 2 }}>
                    <img
                        src="/Underline_02.png"
                        alt="underline"
                        style={{ width: '70%' }}
                    />
                </Box>
                <form onSubmit={handleLogin} style={{width: '100%'}}>
                    <Typography variant="h5" sx={{textAlign: 'left', mb: 1}}>Email</Typography>
                    <TextField
                        required
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        fullWidth
                    />
                    <Typography variant="h5" sx={{textAlign: 'left', mb: 1, mt: 2}}>Password</Typography>
                    <TextField
                        required
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        fullWidth
                    />
                    <ButtonFunky fullWidth sx={{mt: 2}} onClick={handleLogin}>
                        Login
                    </ButtonFunky>
                </form>
                <ButtonFunky
                    onClick={goToSignup} sx={{mt: 2}}
                >
                    Don't have an account? Sign up
                </ButtonFunky>
            </Box>
        </Container>
    );
};

export default LoginPage;