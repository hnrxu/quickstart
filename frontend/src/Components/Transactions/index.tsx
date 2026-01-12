import { useCallback, useEffect, useState } from "react";

const Transactions = () => {
    
    type Transaction = {
        transaction_id: string;
        name: string;
        amount: number;
        merchant_name?: string | null;
        date: string;
    };

    const [transactions, setTransactions] = useState<Transaction[]>([]);

    useEffect(() => {
    const fetchTransactions = async () => {
        const response = await fetch(
        "https://quickstart-lwsu.onrender.com/api/transactions",
        { method: "GET" }
        );

        const transactionData = await response.json();
        setTransactions(transactionData.latest_transactions);
    };

    fetchTransactions();
    }, []);


    // logic of whether or not to show the date header before a transaction
    const showDate = (index: number, date: string) => {   
        if (index ===0) {
            return true;
        }
        if (String(transactions[index - 1].date) === String(date)) {
            return false;
        } return true;
    }

    const formatDate = (date: string) => {
        const formattedDate = new Intl.DateTimeFormat('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'   
        }).format(new Date(`${date.slice(0,4)}-${date.slice(4,6)}-${date.slice(6,8)}`));
        return formattedDate;
    }
    
    return <div>
        {transactions.map((t, index) => (<h1 
        key={t.transaction_id}>
            {showDate(index, t.date) && <div>{formatDate(t.date)}</div>}
            {t.name} {t.amount}</h1>))}
    </div>
}
      


export default Transactions;