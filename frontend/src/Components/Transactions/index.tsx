import { useCallback, useEffect, useState } from "react";
import styles from "./index.module.scss";

const Transactions = () => {
    
    type Transaction = {
        transaction_id: string;
        name: string;
        amount: number;
        merchant_name?: string | null;
        date: dateArray;
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
    const showDate = (index: number, date: dateArray) => {   
        if (index ===0) {
            return true;
        }
        if (String(transactions[index - 1].date) === String(date)) {
            return false;
        } return true;
    }

    type dateArray = [number, number, number]
    const formatDate = (date: dateArray) => {
        const [y, m ,d] = date;
        const formattedDate = new Intl.DateTimeFormat('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'   
        }).format(new Date(y, m-1, d));
        return formattedDate;
    }
    
    return <div>
        {transactions.length}
        {transactions.map((t, index) => (<div 
        
        key={t.transaction_id}>
            {showDate(index, t.date) && <div>{formatDate(t.date)}</div>}
                <div className="transaction-info">
                    {t.name} {t.amount} 
                </div>
            </div>))}
    </div>
}
      


export default Transactions;