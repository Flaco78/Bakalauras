import React, { useEffect, useState } from 'react';
import {
    Dialog, DialogTitle, DialogContent, DialogActions,
    TextField, Button, Box, Typography
} from '@mui/material';
import axios from 'axios';


const ProviderFormDialog = ({ open, onClose, provider, refreshProviders }) => {
    const isEditMode = Boolean(provider);

    const [email, setEmail] = useState('');
    const [name, setName] = useState('');
    const [companyName, setCompanyName] = useState('');
    const [companyCode, setCompanyCode] = useState('');
    const [phone, setPhone] = useState('');
    const [website, setWebsite] = useState('');
    const [password, setPassword] = useState('');
    const [description, setDescription] = useState('');
    const [providerType, setProviderType] = useState('');
    const [individualNameMemory, setIndividualNameMemory] = useState('');
    const [companyNameMemory, setCompanyNameMemory] = useState('');
    const [companyCodeMemory, setCompanyCodeMemory] = useState('');

    useEffect(() => {
        if (isEditMode) {
            setEmail(provider.email || '');
            setName(provider.name || '');
            setPhone(provider.phone || '');
            setWebsite(provider.website || '');
            setDescription(provider.description || '');
            setProviderType(provider.providerType || '');
            setCompanyName(provider.companyName || '');
            setCompanyCode(provider.companyCode || '');
        } else {
            setEmail('');
            setName('');
            setPhone('');
            setWebsite('');
            setPassword('');
            setDescription('');
            setProviderType('');
            setCompanyName('');
            setCompanyCode('');
            setIndividualNameMemory('');
            setCompanyNameMemory('');
            setCompanyCodeMemory('');
        }
    }, [provider]);

    const handleSave = async () => {
        const token = localStorage.getItem('token');

        const payload = {
            email,
            name,
            phone,
            website,
            password: isEditMode ? undefined : password,
            description,
            providerType,
            companyName,
            companyCode,
        };

        console.log(payload);

        try {
            if (isEditMode) {
                await axios.put(`/api/provider/${provider.id}`, payload, {
                    headers: { Authorization: `Bearer ${token}` }
                });
            } else {
                await axios.post('/api/provider/create', payload, {
                    headers: { Authorization: `Bearer ${token}` }
                });
            }
            refreshProviders();
            onClose();
        } catch (error) {
            console.error("Klaida saugant tiekėją:", error);
        }
    };

    const handleClose = () => {
        if (!isEditMode) {
            setEmail('');
            setName('');
            setCompanyName('');
            setCompanyCode('');
            setPhone('');
            setWebsite('');
            setPassword('');
            setDescription('');
            setProviderType('');
            setIndividualNameMemory('');
            setCompanyNameMemory('');
            setCompanyCodeMemory('');
        }

        onClose();
    };

    return (
        <Dialog open={open} onClose={handleClose} fullWidth>
            <DialogTitle>{isEditMode ? 'Redaguoti tiekėją' : 'Naujas tiekėjas'}</DialogTitle>
            <DialogContent>
                <Typography variant="subtitle1" sx={{ mt: 2, mb: 1 }}>Teikėjo tipas</Typography>
                <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mb: 2 }}>
                    <Button
                        variant={providerType === 'INDIVIDUAL' ? 'contained' : 'outlined'}
                        onClick={() => {
                            setCompanyNameMemory(companyName);
                            setCompanyCodeMemory(companyCode);
                            setName(individualNameMemory);
                            setProviderType('INDIVIDUAL');
                        }}
                    >
                        Fizinis asmuo
                    </Button>

                    <Button
                        variant={providerType === 'COMPANY' ? 'contained' : 'outlined'}
                        onClick={() => {
                            setIndividualNameMemory(name);
                            setCompanyName(companyNameMemory);
                            setCompanyCode(companyCodeMemory);
                            setProviderType('COMPANY');
                        }}
                    >
                        Įmonė
                    </Button>
                </Box>
                <TextField fullWidth label="El. paštas" margin="normal" value={email} onChange={(e) => setEmail(e.target.value)} />
                {!isEditMode && (
                    <TextField fullWidth label="Slaptažodis" type="password" margin="normal" value={password} onChange={(e) => setPassword(e.target.value)} />
                )}
                {providerType === 'INDIVIDUAL' && (
                    <TextField
                        fullWidth
                        label="Vardas"
                        margin="normal"
                        value={name}
                        onChange={(e) => {
                            setName(e.target.value);
                            setIndividualNameMemory(e.target.value);
                        }}
                    />
                )}

                {providerType === 'COMPANY' && (
                    <>
                        <TextField
                            fullWidth
                            label="Įmonės pavadinimas"
                            margin="normal"
                            value={companyName}
                            onChange={(e) => {
                                setCompanyName(e.target.value);
                                setCompanyNameMemory(e.target.value);
                            }}
                        />
                        <TextField
                            fullWidth
                            label="Mokesčių mokėtojo kodas"
                            margin="normal"
                            value={companyCode}
                            onChange={(e) => {
                                setCompanyCode(e.target.value);
                                setCompanyCodeMemory(e.target.value);
                            }}
                        />
                    </>
                )}
                <TextField fullWidth label="Telefono numeris" margin="normal" value={phone} onChange={(e) => setPhone(e.target.value)} />
                <TextField fullWidth label="Tinklapis" margin="normal" value={website} onChange={(e) => setWebsite(e.target.value)} />
                <TextField fullWidth label="Aprašymas" margin="normal" multiline rows={3} value={description} onChange={(e) => setDescription(e.target.value)} />
            </DialogContent>
            <DialogActions>
                <Button onClick={handleClose}>Atšaukti</Button>
                <Button variant="contained" onClick={handleSave}>{isEditMode ? 'Išsaugoti' : 'Sukurti'}</Button>
            </DialogActions>
        </Dialog>
    );
};

export default ProviderFormDialog;