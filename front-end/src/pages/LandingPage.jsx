import React from 'react';
import { Box, Typography, Container } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import ButtonFunky from '../components/ButtonFunky.jsx';

const LandingPage = () => {
    const navigate = useNavigate();

    return (
        <Container sx={{mt: 8, textAlign: 'center'}}>
            <Box sx={{width: '100%', display: 'flex', justifyContent: 'center', mb: 3}}>
            <img
                src="/62d21d68efd5226c3d670cd747b26e78.png"
                alt="underline"
                style={{width: '15%'}}
            />
            </Box>
            <Typography variant="h3" sx={{fontFamily: '"Comic Sans MS", cursive', fontWeight: 'bold'}}>
                Welcome to Popamokslis!
            </Typography>
            <Box sx={{width: '100%', display: 'flex', justifyContent: 'center', mb: 2}}>
                <img
                    src="/Underline_01.png"
                    alt="underline"
                    style={{width: '50%'}}
                />
            </Box>
            <Box sx={{display: 'flex', flexDirection: 'column', gap: 2}}>
                <ButtonFunky onClick={() => navigate('/register')}>
                    Join as Parent
                </ButtonFunky>
                <ButtonFunky onClick={() => navigate('/register-provider')}>
                    Join as Provider
                </ButtonFunky>
                <ButtonFunky onClick={() => navigate('/login')}>
                    Already have an account? Log in
                </ButtonFunky>
            </Box>
        </Container>
    );
};

export default LandingPage;