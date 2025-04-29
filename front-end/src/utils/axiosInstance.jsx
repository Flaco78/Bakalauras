import axios from 'axios';
import { toast } from 'react-toastify';

const instance = axios.create({
    baseURL: 'http://localhost:8080',
    headers: {
        'Content-Type': 'application/json',
    },
});

instance.interceptors.response.use(
    response => response,
    error => {
        if (error.response && error.response.status === 401) {
            toast.error('Sesija baigėsi. Prisijunkite iš naujo.');
            setTimeout(() => {
                window.location.href = '/';
            }, 2000);
        }
        return Promise.reject(error);
    }
);

export default instance;