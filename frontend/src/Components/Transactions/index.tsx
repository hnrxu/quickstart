import { useCallback, useEffect, useState } from "react";
import styles from "./index.module.scss";
import Dashboard, { Transaction } from "../Dashboard";

type TransactionsProps = {
  transactions: Transaction[];
};

const Transactions = ({transactions}: TransactionsProps) => {
    

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

    const formatAmount = (amount: number) => {
        const stringAmount = String(amount);
        let formattedAmount = "";
        if (amount < 0) {
            formattedAmount = `${stringAmount.substring(0,1)}$${stringAmount.substring(1)}`
        } else {
            formattedAmount = `$${stringAmount}`
        }
        return formattedAmount;
    }

    const filterTransactions = (categories: string []) => 
        transactions.filter((t, index) => 
            categories.includes(t.personal_finance_category.primary)
        )
      
    

    if (!transactions || transactions.length === 0) {
        return <div>Loading...</div>;
    }

    return <div> 
        <div className={styles.title}><b>Transactions</b></div>
        <div className={styles.transactionContainerShadow}>
            
            
                
            

            <div className={styles.transactionContainer}>
                <div className={styles.scrollWrapper}>
                    {transactions.map((t, index) => (<div 
                    key={t.transaction_id}>
                            {showDate(index, t.date) && <div className={styles.dateContainer}>{formatDate(t.date)}</div>}
                        
    
                        <div className={styles.transactionInfoWrapper}>
                            <div className={styles.transactionInfo}>  
                                <div className={styles.name}>{t.name}</div> <div className={styles.amount}>{formatAmount(t.amount)} </div>     
                            </div>

                        </div>
                       
                        </div>))}
                </div>
            </div>


        </div>
    </div>
}
      


export default Transactions;