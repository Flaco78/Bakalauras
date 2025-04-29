import React, { useEffect, useState } from 'react';
import {
    Button, Box, Typography, Table, TableHead,
    TableRow, TableCell, TableBody, IconButton
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import axios from 'axios';
import ProviderFormDialog from '../../forms/ProviderFormDialog';

const ProviderTab = () => {
    const [providers, setProviders] = useState([]);
    const [openDialog, setOpenDialog] = useState(false);
    const [selectedProvider, setSelectedProvider] = useState(null);

    const fetchProviders = async () => {
        try {
            const response = await axios.get('/api/provider/all', {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('token')}`,
                },
            });
            setProviders(response.data);
        } catch (error) {
            console.error('Nepavyko gauti tiekėjų:', error);
        }
    };

    useEffect(() => {
        fetchProviders();
    }, []);

    const handleEdit = (provider) => {
        setSelectedProvider(provider);
        setOpenDialog(true);
    };

    const handleCreate = () => {
        setSelectedProvider(null);
        setOpenDialog(true);
    };

    const handleDelete = async (id) => {
        try {
            await axios.delete(`/api/provider/${id}`, {
                headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
            });
            fetchProviders();
        } catch (error) {
            console.error('Nepavyko ištrinti tiekėjo:', error);
        }
    };

    return (
        <Box p={2}>
            <Typography variant="h6" sx={{ mb: 2 }}>Tiekėjų administravimas</Typography>
            <Button variant="contained" onClick={handleCreate} sx={{ mb: 2 }}>
                ➕ Naujas tiekėjas
            </Button>

            <Table>
                <TableHead>
                    <TableRow>
                        <TableCell>El. paštas</TableCell>
                        <TableCell>Pavadinimas</TableCell>
                        <TableCell>Telefono numeris</TableCell>
                        <TableCell>Tiekėjo tipas</TableCell>
                        <TableCell>Mokesčių mokėtojo kodas</TableCell>
                        <TableCell>Veiksmai</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {providers.map((provider) => (
                        <TableRow key={provider.id}>
                            <TableCell>{provider.email}</TableCell>
                            <TableCell>
                                {provider.providerType === 'COMPANY' ? provider.companyName : provider.name}
                            </TableCell>
                            <TableCell>{provider.phone}</TableCell>
                            <TableCell>
                                {provider.providerType === 'COMPANY' ? 'Įmonė' : 'Fizinis asmuo'}
                            </TableCell>
                            <TableCell>{provider.companyCode}</TableCell>
                            <TableCell>
                                <IconButton onClick={() => handleEdit(provider)}>
                                    <EditIcon fontSize="small" />
                                </IconButton>
                                <IconButton color="error" onClick={() => handleDelete(provider.id)}>
                                    <DeleteIcon fontSize="small" />
                                </IconButton>
                            </TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>

            <ProviderFormDialog
                open={openDialog}
                onClose={() => setOpenDialog(false)}
                provider={selectedProvider}
                refreshProviders={fetchProviders}
            />
        </Box>
    );
};

export default ProviderTab;