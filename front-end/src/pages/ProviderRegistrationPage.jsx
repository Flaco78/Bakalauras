import React, { useState } from 'react';
import {Container, Box, Typography, InputLabel, MenuItem, Select, FormControl} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import ButtonFunky from "../components/ButtonFunky.jsx";
import TextFieldFunky from "../components/TextFieldFunky.jsx";

const ProviderRegistrationPage = () => {
    const [form, setForm] = useState({
        name: '',
        email: '',
        password: '',
        confirmPassword: '',
        description: '',
        phone: '',
        website: '',
        providerType: ''
    });

    const navigate = useNavigate();

    const handleChange = (e) => {
        const { name, value } = e.target;
        setForm(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (form.password !== form.confirmPassword) {
            alert("Passwords do not match");
            return;
        }

        try {
            const { confirmPassword, ...requestData } = form;

            const response = await axios.post('/api/provider-request/register', requestData, {
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            if (response.status === 200 || response.status === 201) {
                alert("Request submitted successfully!");
                navigate('/login');
            } else {
                alert("Submission failed.");
            }
        } catch (error) {
            console.error("Submission error:", error);
            alert("Something went wrong. Try again.");
        }
    };

    return (
        <Container component="main" maxWidth="xs">
            <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', mt: 5 }}>
                <Typography variant="h3" sx={{ fontFamily: '"Comic Sans MS", cursive', fontWeight: 'bold', color: 'rgba(0,0,0,0.87)' }}>
                    PROVIDER SIGNUP
                </Typography>
                <Box sx={{ width: '100%', display: 'flex', justifyContent: 'center', mb: 2 }}>
                    <img src="/Underline_01.png" alt="underline" style={{ width: '70%' }} />
                </Box>
                <form onSubmit={handleSubmit} style={{ width: '100%' }}>
                    <Typography variant="h6" sx={{ textAlign: 'left', mb: 1 }}>Name</Typography>
                    <TextFieldFunky name="name" required fullWidth value={form.name} onChange={handleChange} />

                    <Typography variant="h6" sx={{ textAlign: 'left', mb: 1, mt: 2 }}>Email</Typography>
                    <TextFieldFunky name="email" required fullWidth value={form.email} onChange={handleChange} />

                    <Typography variant="h6" sx={{ textAlign: 'left', mb: 1, mt: 2 }}>Password</Typography>
                    <TextFieldFunky name="password" type="password" required fullWidth value={form.password} onChange={handleChange} />

                    <Typography variant="h6" sx={{ textAlign: 'left', mb: 1, mt: 2 }}>Confirm Password</Typography>
                    <TextFieldFunky name="confirmPassword" type="password" required fullWidth value={form.confirmPassword} onChange={handleChange} />

                    <Typography variant="h6" sx={{ textAlign: 'left', mb: 1, mt: 2 }}>Phone</Typography>
                    <TextFieldFunky name="phone" required fullWidth value={form.phone} onChange={handleChange} />

                    <Typography variant="h6" sx={{ textAlign: 'left', mb: 1, mt: 2 }}>Website</Typography>
                    <TextFieldFunky name="website" fullWidth value={form.website} onChange={handleChange} />

                    <Typography variant="h6" sx={{ textAlign: 'left', mb: 1, mt: 2 }}>Description</Typography>
                    <TextFieldFunky name="description" multiline rows={3} required fullWidth value={form.description} onChange={handleChange} />

                    <Typography variant="h6" sx={{ textAlign: 'left', mb: 1, mt: 2 }}>Provider Type</Typography>
                    <FormControl fullWidth required>
                        <InputLabel>Provider Type</InputLabel>
                        <Select
                            name="providerType"
                            value={form.providerType}
                            onChange={handleChange}
                            label="Provider Type"
                        >
                            <MenuItem value="INDIVIDUAL">Individual</MenuItem>
                            <MenuItem value="COMPANY">Company</MenuItem>
                        </Select>
                    </FormControl>

                    {form.providerType === 'COMPANY' && (
                        <>
                            <Typography variant="h6" sx={{ textAlign: 'left', mb: 1, mt: 2 }}>Company Name</Typography>
                            <TextFieldFunky name="companyName" fullWidth value={form.companyName} onChange={handleChange} />

                            <Typography variant="h6" sx={{ textAlign: 'left', mb: 1, mt: 2 }}>Tax ID</Typography>
                            <TextFieldFunky name="taxId" fullWidth value={form.taxId} onChange={handleChange} />
                        </>
                    )}
                    <ButtonFunky fullWidth sx={{ mt: 3 }} onClick={handleSubmit}>
                        Submit Request
                    </ButtonFunky>
                </form>
            </Box>
        </Container>
    );
};

export default ProviderRegistrationPage;
