import React, { useState } from 'react';
import {Container, Box, Typography, FormControlLabel, Checkbox,} from '@mui/material';
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
    const [errors, setErrors] = useState({});
    const navigate = useNavigate();
    const [agreedToTerms, setAgreedToTerms] = useState(false);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setForm(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!agreedToTerms) {
            alert("Turite sutikti su privatumo politika, kad galėtumėte registruotis.");
            return;
        }

        const newErrors = {};

        if (!form.email) newErrors.email = 'El. paštas yra privalomas';
        if (!form.password) newErrors.password = 'Slaptažodis yra privalomas';
        if (!form.confirmPassword) newErrors.confirmPassword = 'Pakartokite slaptažodį';
        if (form.password !== form.confirmPassword) newErrors.confirmPassword = 'Slaptažodžiai nesutampa';
        if (!form.description) newErrors.description = 'Aprašymas yra privalomas';
        if (!form.providerType) newErrors.providerType = 'Pasirinkite teikėjo tipą';

        if (form.providerType === 'COMPANY') {
            if (!form.companyName) newErrors.companyName = 'Įmonės pavadinimas yra privalomas';
            if (!form.companyCode) newErrors.companyCode = 'Mokesčių mokėtojo kodas yra privalomas';
        }

        if (form.providerType === 'INDIVIDUAL') {
            if (!form.name) newErrors.name = 'Vardas yra privalomas';
        }

        if (Object.keys(newErrors).length > 0) {
            setErrors(newErrors);
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
                setErrors({});
                alert("Registracija sėkminga!");
                navigate('/login');
            } else {
                setErrors({ general: "Nepavyko pateikti užklausos." });
            }
        } catch (error) {
            if (error.response && error.response.status === 400 && typeof error.response.data === 'object') {
                setErrors(error.response.data);
            } else {
                setErrors({ general: "Įvyko klaida. Bandykite dar kartą." });
            }
        }
    };

    return (
        <Container component="main" maxWidth="xs">
            <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', mt: 5 }}>
                <Typography variant="h3" sx={{ fontFamily: '"Comic Sans MS", cursive', fontWeight: 'bold', color: 'rgba(0,0,0,0.87)' }}>
                     PRISIJUNGIMAS
                </Typography>
                <Box sx={{ width: '100%', display: 'flex', justifyContent: 'center', mb: 2 }}>
                    <img src="/Underline_01.png" alt="underline" style={{ width: '70%' }} />
                </Box>

                {Object.values(errors).some(msg => msg) && (
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

                <form onSubmit={handleSubmit} style={{ width: '100%' }}>
                    <Typography variant="h6" sx={{ textAlign: 'center', mb: 1, mt: 2 }}>Teikėjo tipas</Typography>
                    <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mb: 3 }}>
                        <ButtonFunky
                            sx={{
                                backgroundColor: form.providerType === 'INDIVIDUAL' ? '#81c784' : '#ef9a9a',
                                border: '2px solid #422800',
                                fontWeight: 'bold',
                            }}
                            onClick={() => {
                                setForm(prev => ({
                                    ...prev,
                                    providerType: 'INDIVIDUAL',
                                    name: '',
                                    companyName: '',
                                    companyCode: ''
                                }));
                                setErrors(prev => ({ ...prev, providerType: null }));
                            }}
                        >
                            Fizinis asmuo
                        </ButtonFunky>

                        <ButtonFunky
                            sx={{
                                backgroundColor: form.providerType === 'COMPANY' ? '#81c784' : '#ef9a9a',
                                border: '2px solid #422800',
                                fontWeight: 'bold',
                            }}
                            onClick={() => {
                                setForm(prev => ({
                                    ...prev,
                                    providerType: 'COMPANY',
                                    name: '',
                                    companyName: '',
                                    companyCode: ''
                                }));
                                setErrors(prev => ({ ...prev, providerType: null }));
                            }}
                        >
                            Įmonė
                        </ButtonFunky>
                    </Box>

                    {form.providerType === 'INDIVIDUAL' && (
                        <>
                            <Typography variant="h6" sx={{ textAlign: 'left', mb: 1 }}>Vardas</Typography>
                            <TextFieldFunky
                                name="name"
                                fullWidth
                                value={form.name}
                                onChange={(e) => {
                                    handleChange(e);
                                    setErrors(prev => ({ ...prev, name: null }));
                                }}
                            />
                        </>
                    )}

                    <Typography variant="h6" sx={{ textAlign: 'left', mb: 1, mt: 2 }}>El. paštas</Typography>
                    <TextFieldFunky
                        name="email"
                        fullWidth
                        value={form.email}
                        onChange={handleChange}
                    />

                    <Typography variant="h6" sx={{ textAlign: 'left', mb: 1, mt: 2 }}>Slaptažodis</Typography>
                    <TextFieldFunky
                        name="password"
                        type="password"
                        fullWidth
                        value={form.password}
                        onChange={handleChange}
                    />

                    <Typography variant="h6" sx={{ textAlign: 'left', mb: 1, mt: 2 }}>Patvirtinti slaptažodį</Typography>
                    <TextFieldFunky
                        name="confirmPassword"
                        type="password"
                        fullWidth
                        value={form.confirmPassword}
                        onChange={handleChange}
                    />

                    <Typography variant="h6" sx={{ textAlign: 'left', mb: 1, mt: 2 }}>Telefono nr.</Typography>
                    <TextFieldFunky
                        name="phone"
                        fullWidth
                        value={form.phone}
                        onChange={handleChange}
                    />

                    <Typography variant="h6" sx={{ textAlign: 'left', mb: 1, mt: 2 }}>Puslapis</Typography>
                    <TextFieldFunky
                        name="website"
                        fullWidth
                        value={form.website}
                        onChange={handleChange}
                    />

                    <Typography variant="h6" sx={{ textAlign: 'left', mb: 1, mt: 2 }}>Apie save</Typography>
                    <TextFieldFunky
                        name="description"
                        multiline
                        rows={3}
                        required
                        fullWidth
                        value={form.description}
                        onChange={handleChange}
                    />
                    {form.providerType === 'COMPANY' && (
                        <>
                            <Typography variant="h6" sx={{ textAlign: 'left', mb: 1, mt: 2 }}>Kompanijos pavadinimas</Typography>
                            <TextFieldFunky name="companyName" fullWidth value={form.companyName} onChange={handleChange} />

                            <Typography variant="h6" sx={{ textAlign: 'left', mb: 1, mt: 2 }}>Įmonės kodas</Typography>
                            <TextFieldFunky name="companyCode" fullWidth value={form.companyCode} onChange={handleChange} />
                        </>
                    )}
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
                    <ButtonFunky fullWidth sx={{ mt: 3 }} onClick={handleSubmit}>
                        Išsaugoti užklausą
                    </ButtonFunky>
                </form>
            </Box>
        </Container>
    );
};

export default ProviderRegistrationPage;
