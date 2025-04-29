import React, { useEffect, useState } from 'react';
import {
    Button, Box, Typography, Table, TableHead,
    TableRow, TableCell, TableBody, IconButton
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import axios from 'axios';
import UserFormDialog from '../../forms/UserFormDialog';

const UsersTab = () => {
    const [users, setUsers] = useState([]);
    const [openDialog, setOpenDialog] = useState(false);
    const [selectedUser, setSelectedUser] = useState(null);

    // Fetch users on component mount
    const fetchUsers = async () => {
        try {
            const response = await axios.get('/api/users/all', {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('token')}`,
                },
            });
            setUsers(response.data);
        } catch (error) {
            console.error('Nepavyko gauti vartotojų:', error);
        }
    };

    useEffect(() => {
        fetchUsers();
    }, []);

    const handleEdit = (user) => {
        setSelectedUser(user);
        setOpenDialog(true);
    };

    const handleCreate = () => {
        setSelectedUser(null);
        setOpenDialog(true);
    };

    const handleDelete = async (id) => {
        const token = localStorage.getItem('token');
        try {
            await axios.delete(`/api/users/${id}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            fetchUsers();  // Re-fetch the users after deleting
        } catch (error) {
            console.error('Nepavyko ištrinti vartotojo:', error);
        }
    };

    return (
        <Box p={2}>
            <Typography variant="h6" sx={{ mb: 2 }}>Vartotojų administravimas</Typography>
            <Button variant="contained" onClick={handleCreate} sx={{ mb: 2 }}>
                ➕ Naujas vartotojas
            </Button>

            {/* Inline user table */}
            <Table>
                <TableHead>
                    <TableRow>
                        <TableCell>El. paštas</TableCell>
                        <TableCell>Adresas</TableCell>
                        <TableCell>Rolės</TableCell>
                        <TableCell>Veiksmai</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {users.map((user) => (
                        <TableRow key={user.id}>
                            <TableCell>{user.email}</TableCell>
                            <TableCell>{user.address}</TableCell>
                            <TableCell>{user.roles ? user.roles.join(', ') : ''}</TableCell>
                            <TableCell>
                                <IconButton onClick={() => handleEdit(user)}>  <EditIcon fontSize="small" /></IconButton>
                                <IconButton color="error" onClick={() => handleDelete(user.id)}><DeleteIcon fontSize="small" /></IconButton>
                            </TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>

            {/* Dialogas redagavimui ir kūrimui */}
            <UserFormDialog
                open={openDialog}
                onClose={() => setOpenDialog(false)}
                user={selectedUser}
                refreshUsers={fetchUsers} // Refresh the users after creating or updating a user
            />
        </Box>
    );
};

export default UsersTab;