import { useCallback, useEffect, useState } from "react";
import styles from "./index.module.scss";
import Dashboard, { Transaction } from "../Dashboard";

type TransactionsProps = {
  transactions: Transaction[];
};

const Transactions = ({transactions}: TransactionsProps) => {
    
    // type Transaction = {
    //     transaction_id: string;
    //     name: string;
    //     amount: number;
    //     merchant_name?: string | null;
    //     date: dateArray;
    // };

    // type Summary = {
    //     most_frequent_category: {
    //         name: string;
    //         total_spent: number;
    //         num_purchases: number;
    //     };
    //     most_spent_category: {
    //         name: string;
    //         total_spent: number;
    //         num_purchases: number;
    //     };
    // };

    // const [transactions, setTransactions] = useState<Transaction[]>([]);
    // const [summaries, setSummaries] = useState<Summary|null>(null);

    // useEffect(() => {
    // const fetchTransactions = async () => {
    //     const response = await fetch(
    //     "https://quickstart-lwsu.onrender.com/api/transactions",
    //     { method: "GET" }
    //     );

    //     const transactionData = await response.json();
    //     setTransactions(transactionData.latest_transactions);
       

    // };

    // fetchTransactions();

    // const fetchSummaryData = async () => {
    //     const response = await fetch("https://quickstart-lwsu.onrender.com/api/summarydata",
    //         { method: "GET"});
    //     if (!response.ok) {
    //         console.error("summarydata failed", response.status);
    //         return;
    //     }
    //     const summaryData = await response.json();
    //     setSummaries(summaryData);
    // }

    // fetchSummaryData();
    // }, []);


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

    {if(transactions.length === 0) {return <div> loading...</div>}}

    return <div> 
        Viewing Transactions 
        {transactions.map((t, index) => (<div 
        key={t.transaction_id}>
            {showDate(index, t.date) && <div>{formatDate(t.date)}</div>}
                <p className={styles.transactionInfo}>
                    {t.name} {t.amount} 
                </p>
            </div>))}
            hdllohdllodhllooooo
    </div>
}
      


export default Transactions;