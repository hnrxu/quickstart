import styles from "./index.module.scss";

const Loading = () => {
    return (
        <div className={styles.overlay}>
        <div className={styles.container}>
            <div className={styles.text}>LOADING...</div>
                <div className={styles.bar}>
                    <div className={styles.fill} />
                </div>
 
            
        </div>
        </div>
    );
}

export default Loading;