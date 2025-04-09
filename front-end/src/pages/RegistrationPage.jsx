import React, { useState } from 'react';
import { Container, Box, Typography } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import ButtonFunky from "../components/ButtonFunky.jsx";
import TextFieldFunky from "../components/TextFieldFunky.jsx";

const RegistrationPage = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const navigate = useNavigate();

    const handleSignup = async (e) => {
        e.preventDefault();

        if (password !== confirmPassword) {
            alert("Passwords don't match");
            return;
        }

        try {
            const response = await axios.post('/api/auth/register', {
                email,
                password
            });

            if (response.status === 200) {
                navigate('/login');
            } else {
                alert(`Registration failed: ${response.data.message || 'Unknown error'}`);
            }
        } catch (error) {
            console.error("There was an error registering:", error);
            alert("Registration failed. Please try again.");
        }
    };

    return (
        <Container component="main" maxWidth="xs">
            <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', mt: 5 }}>
                <Typography variant="h3" sx={{ fontFamily: '"Comic Sans MS", cursive, sans-serif', fontWeight: 'bold', color: 'rgba(0,0,0,0.87)' }}>
                    SIGNUP
                </Typography>
                <Box sx={{ width: '100%', display: 'flex', justifyContent: 'center', mb: 2 }}>
                    <img
                        src="/Underline_01.png"
                        alt="underline"
                        style={{ width: '70%' }}
                    />
                </Box>
                <form onSubmit={handleSignup} style={{width: '100%'}}>
                    <Typography variant="h5" sx={{textAlign: 'left', mb: 1}}>Email</Typography>
                    <TextFieldFunky
                        required
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        fullWidth
                    />
                    <Typography variant="h5" sx={{textAlign: 'left', mb: 1, mt: 2}}>Password</Typography>
                    <TextFieldFunky
                        required
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        fullWidth
                    />
                    <Typography variant="h5" sx={{textAlign: 'left', mb: 1, mt: 2}}>Confirm Password</Typography>
                    <TextFieldFunky
                        required
                        type="password"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        fullWidth
                    />
                    <ButtonFunky fullWidth sx={{mt: 2}}>
                        Sign Up
                    </ButtonFunky>
                </form>
            </Box>
        </Container>
    );
};

export default RegistrationPage;