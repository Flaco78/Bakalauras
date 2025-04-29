import React, { useEffect, useState } from 'react';
import {
    Dialog, DialogTitle, DialogContent, DialogActions,
    Button, TextField, MenuItem
} from '@mui/material';
import axios from 'axios';
import { categories, priceTypes, deliveryMethods } from '../utils/constants';
import {useAuth} from "../context/AuthContext.jsx";


const ActivityFormDialog = ({ open, onClose, activity, onSave }) => {
    const isEdit = Boolean(activity);
    const user = JSON.parse(localStorage.getItem('user'));
    const [formData, setFormData] = useState({
        title: '',
        description: '',
        location: '',
        category: '',
        price: '',
        descriptionChild: '',
        imageUrl: '',
        durationMinutes: '',
        priceType: 'MONTHLY',
        deliveryMethod: 'ONSITE',
    });
    const [providers, setProviders] = useState([]);
    const { roles } = useAuth();

    useEffect(() => {
        if (open) {
            // Fetch providers for admin
            if (roles.includes('ADMIN')) {
                axios.get('/api/provider/all', {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem('token')}`
                    }
                }).then(res => {
                    setProviders(res.data);
                }).catch(err => {
                    setProviders([]);
                });
            } else {
                // For providers, set providerId automatically
                const user = JSON.parse(localStorage.getItem('user'));
                if (user && user.id) {
                    setFormData(prev => ({
                        ...prev,
                        providerId: user.id
                    }));
                }
            }
        }
    }, [open, roles]);

    useEffect(() => {
        if (activity) {
            setFormData({ ...activity });
        } else {
            setFormData({
                title: '',
                description: '',
                location: '',
                category: '',
                price: '',
                descriptionChild: '',
                imageUrl: '',
                durationMinutes: '',
                priceType: 'MONTHLY',
                deliveryMethod: 'ONSITE',
                providerId: roles.includes('ADMIN') ? '' : (user && user.id ? user.id : '')
        });
        }
    }, [activity]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleSubmit = async () => {
        console.log('Submitting formData:', formData)
        try {
            const url = isEdit
                ? (`/api/activities/admin/${activity.id}`)
                : ('/api/activities/admin/create');

            const method = isEdit ? 'put' : 'post';

            const response = await axios[method](url, formData, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('token')}`,
                },
            });

            onSave(response.data);
            onClose();
        } catch (err) {
            console.error('Klaida išsaugant veiklą:', err);
            alert('Nepavyko išsaugoti veiklos.');
        }
    };

    return (
        <Dialog open={open} onClose={onClose} fullWidth>
            <DialogTitle>{isEdit ? 'Redaguoti veiklą' : 'Sukurti veiklą'}</DialogTitle>
            <DialogContent>
                <TextField
                    fullWidth
                    margin="dense"
                    label="Pavadinimas"
                    name="title"
                    value={formData.title}
                    onChange={handleChange}
                />
                <TextField
                    fullWidth
                    margin="dense"
                    label="Aprašymas"
                    name="description"
                    value={formData.description}
                    onChange={handleChange}
                />
                <TextField
                    fullWidth
                    margin="dense"
                    label="Lokacija"
                    name="location"
                    value={formData.location}
                    onChange={handleChange}
                />
                <TextField
                    fullWidth
                    margin="dense"
                    label="Kaina"
                    type="number"
                    name="price"
                    value={formData.price}
                    onChange={handleChange}
                />
                <TextField
                    fullWidth
                    margin="dense"
                    label="Vaikams skirtas aprašymas"
                    name="descriptionChild"
                    value={formData.descriptionChild}
                    onChange={handleChange}
                />

                <TextField
                    fullWidth
                    margin="dense"
                    label="Nuotraukos URL"
                    name="imageUrl"
                    value={formData.imageUrl}
                    onChange={handleChange}
                />

                <TextField
                    fullWidth
                    margin="dense"
                    label="Trukmė (min.)"
                    type="number"
                    name="durationMinutes"
                    value={formData.durationMinutes}
                    onChange={handleChange}
                />
                <TextField
                    fullWidth
                    margin="dense"
                    label="Kategorija"
                    name="category"
                    select
                    value={formData.category}
                    onChange={handleChange}
                >
                    {categories.map((cat) => (
                        <MenuItem key={cat.value} value={cat.value}>
                            {cat.label}
                        </MenuItem>
                    ))}
                </TextField>

                <TextField
                    fullWidth
                    margin="dense"
                    label="Kainos tipas"
                    name="priceType"
                    select
                    value={formData.priceType}
                    onChange={handleChange}
                >
                    {priceTypes.map((type) => (
                        <MenuItem key={type.value} value={type.value}>
                            {type.label}
                        </MenuItem>
                    ))}
                </TextField>

                <TextField
                    fullWidth
                    margin="dense"
                    label="Užsiėmimo būdas"
                    name="deliveryMethod"
                    select
                    value={formData.deliveryMethod}
                    onChange={handleChange}
                >
                    {deliveryMethods.map((method) => (
                        <MenuItem key={method.value} value={method.value}>
                            {method.label}
                        </MenuItem>
                    ))}
                </TextField>
                {roles.includes('ADMIN') && (
                <TextField
                    fullWidth
                    margin="dense"
                    label="Tiekėjas"
                    name="providerId"
                    select
                    value={formData.providerId || ''}
                    onChange={handleChange}
                >
                    {Array.isArray(providers) && providers.map((provider) => (
                        <MenuItem key={provider.id} value={provider.id}>
                            {provider.providerType === 'COMPANY' ? '🏢 ' : '👤 '}
                            {provider.providerType === 'COMPANY'
                                ? provider.companyName || provider.email
                                : provider.name || provider.email}
                        </MenuItem>
                    ))}
                </TextField>
                )}
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose}>Atšaukti</Button>
                <Button onClick={handleSubmit} variant="contained">
                    {isEdit ? 'Išsaugoti' : 'Sukurti'}
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default ActivityFormDialog;