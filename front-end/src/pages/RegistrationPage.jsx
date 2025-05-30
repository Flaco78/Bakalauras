import React, { useState } from 'react';
import { Container, Box, Typography } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import ButtonFunky from "../components/ButtonFunky.jsx";
import TextFieldFunky from "../components/TextFieldFunky.jsx";
import { Checkbox, FormControlLabel } from '@mui/material';

const RegistrationPage = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [address, setAddress] = useState('');
    const navigate = useNavigate();
    const [errors, setErrors] = useState({});
    const [agreedToTerms, setAgreedToTerms] = useState(false);


    const handleSignup = async (e) => {
        e.preventDefault();

        if (!agreedToTerms) {
            alert("Turite sutikti su privatumo politika, kad galėtumėte registruotis.");
            return;
        }

        if (password !== confirmPassword) {
            alert("Passwords don't match");
            return;
        }

        try {
            const response = await axios.post('/api/auth/register', {
                email,
                password,
                address,
            });

            if (response.status === 200) {
                navigate('/login');
            } else {
                alert(`Registration failed: ${response.data.message || 'Unknown error'}`);
            }
        } catch (error) {
            if (
                error.response &&
                error.response.status === 400 &&
                typeof error.response.data === 'object'
            ) {
                setErrors(error.response.data);
            } else {
                setErrors({ general: 'Įvyko nenumatyta klaida. Bandykite dar kartą.' });
            }
    }
    };

    return (
        <Container component="main" maxWidth="xs">
            <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', mt: 5 }}>
                <Typography variant="h3" sx={{ fontFamily: '"Comic Sans MS", cursive, sans-serif', fontWeight: 'bold', color: 'rgba(0,0,0,0.87)' }}>
                    REGISTRACIJA
                </Typography>
                <Box sx={{ width: '100%', display: 'flex', justifyContent: 'center', mb: 2 }}>
                    <img
                        src="/Underline_01.png"
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
                <form onSubmit={handleSignup} style={{width: '100%'}}>
                    <Typography variant="h5" sx={{textAlign: 'left', mb: 1}}>El. paštas</Typography>
                    <TextFieldFunky
                        value={email}
                        onChange={(e) => {
                            setEmail(e.target.value);
                            setErrors({});
                        }}
                        fullWidth
                    />
                    <Typography variant="h5" sx={{textAlign: 'left', mb: 1, mt: 2}}>Slaptažodis</Typography>
                    <TextFieldFunky
                        type="password"
                        value={password}
                        onChange={(e) => {
                            setPassword(e.target.value);
                            setErrors({});
                        }}
                        fullWidth
                    />
                    <Typography variant="h5" sx={{textAlign: 'left', mb: 1, mt: 2}}>Pakartokite slaptažodį</Typography>
                    <TextFieldFunky
                        type="password"
                        value={confirmPassword}
                        onChange={(e) => {
                            setConfirmPassword(e.target.value);
                            setErrors({});
                        }}
                        fullWidth
                    />
                    <Typography variant="h5" sx={{ textAlign: 'left', mb: 1, mt: 2 }}>Adresas</Typography>
                    <TextFieldFunky
                        value={address}
                        onChange={(e) => {
                            setAddress(e.target.value);
                            setErrors({});
                        }}
                        fullWidth
                    />
                    <FormControlLabel
                        control={
                            <Checkbox
                                checked={agreedToTerms}
                                onChange={(e) => setAgreedToTerms(e.target.checked)}
                                color="primary"
                            />
                        }
                        label={
                            <Typography variant="body2">
                                Sutinku su <a href="/privatumo-politika" target="_blank" rel="noopener noreferrer">privatumo politika</a>
                            </Typography>
                        }
                    />
                    <ButtonFunky type="submit" fullWidth sx={{mt: 2}}>
                        Registruotis
                    </ButtonFunky>
                </form>
            </Box>
        </Container>
    );
};

export default RegistrationPage;