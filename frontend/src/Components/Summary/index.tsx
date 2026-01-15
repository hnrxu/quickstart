import Dashboard, { Category, Store } from "../Dashboard";
import styles from "./index.module.scss";
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Cell } from "recharts";




type SummaryProps = {
    freqCategories: Category[];
    spentCategories: Category[];
    freqStores: Store[];
    spentStores: Store[];
}
const SummaryWidget = ({freqCategories, spentCategories, freqStores, spentStores}: SummaryProps) => {

   
    console.log(freqCategories)
    const getColorFromCss = (cssColor: string) => {

        return getComputedStyle(document.documentElement)
        .getPropertyValue(cssColor)
        .trim();
    }
    
    const barColors = [getColorFromCss("--main-color-bar-1"),
                        getColorFromCss("--main-color-bar-2"),
                        getColorFromCss("--main-color-bar-3"),
                        getColorFromCss("--main-color-bar-4"),
                        getColorFromCss("--main-color-bar-5")
                    ];
   

    if(freqCategories.length === 0 || spentCategories.length === 0 ) {return <div> loading...</div>}


    return <div className={styles.outerContainer}>
        <div className={styles.summariesContainer}>


            <div className={styles.catfreqChartContainer}>
                <div className={styles.title}>
                    Most Frequent Expense Categories
                </div>
                <div className={styles.catfreqChart}>
                    <div className={styles.catFreqRank}>
            
                        {freqCategories.slice(0,3).map((c, index) => (
                        <div key={index} className={styles.rankBox}>
                            <span className={`${styles.rankBadge} ${styles[`rank${index + 1}`]}`}>{index + 1}</span>
                            <span className={styles.rankLabel}>{c.name}</span>
                        </div>
                        
                        ))}
                    </div>
                    <ResponsiveContainer width="100%" height={300}>
                        <BarChart  data={freqCategories.slice(0,5)}>
                            <XAxis 
                            dataKey="name" 
                            interval={0} 
                            style={{
                                fontFamily: "Inter",
                                fontSize: 15
                                //fontWeight: 250
                                }} 
                            stroke="black" 
                            tick={{ fill: 'black'}} 
                            tickFormatter={(value) => `${value.slice(0, 7)}...`} 
                            ></XAxis>
                            <YAxis stroke="black" tick={{ fill: 'black' }} />
                            
                            <Bar dataKey="num_purchases" fill="#b684d8ff" >
                                {
                                    freqCategories.map((bar, index) => (
                                        <Cell key={index} fill={barColors[index % barColors.length]} />
                                        
                                    ))
                                }
                            </Bar> 
                        </BarChart>
                    </ResponsiveContainer>
                </div>
            </div>



            <div className={styles.catfreqChartContainer}>
                <div className={styles.title}>
                    Most Frequently Visited Stores
                </div>
                <div className={styles.catfreqChart}>
                    <div className={styles.catFreqRank}>
            
                        {freqStores.slice(0,3).map((s, index) => (
                        <div key={index} className={styles.rankBox}>
                            <span className={`${styles.rankBadge} ${styles[`rank${index + 1}`]}`}>{index + 1}</span>
                            <span className={styles.rankLabel}>{s.name}</span>
                        </div>
                        
                        ))}
                    </div>
                    <ResponsiveContainer width="100%" height={300}>
                        <BarChart  data={freqStores.slice(0,5)}>
                            <XAxis 
                            dataKey="name" 
                            interval={0} 
                            style={{
                                fontFamily: "Inter",
                                //fontWeight: 250
                                }} 
                            stroke="black" 
                            tick={{ fill: 'black'}} 
                            tickFormatter={(value) => `${value.slice(0, 7)}...`} 
                            ></XAxis>
                            <YAxis stroke="black" tick={{ fill: 'black' }} />
                            
                            <Bar dataKey="num_purchases" fill="#b684d8ff" >
                                {
                                    freqCategories.map((bar, index) => (
                                        <Cell key={index} fill={barColors[index % barColors.length]} />
                                        
                                    ))
                                }
                            </Bar> 
                        </BarChart>
                    </ResponsiveContainer>
                </div>
            </div>



            <div className={styles.catfreqChartContainer}>
                <div className={styles.title}>
                    Categories Most Spent At
                </div>
                <div className={styles.catfreqChart}>
                    <div className={styles.catFreqRank}>
            
                        {spentCategories.slice(0,3).map((c, index) => (
                        <div key={index} className={styles.rankBox}>
                            <span className={`${styles.rankBadge} ${styles[`rank${index + 1}`]}`}>{index + 1}</span>
                            <span className={styles.rankLabel}>{c.name}</span>
                        </div>
                        
                        ))}
                    </div>
                    <ResponsiveContainer width="100%" height={300}>
                        <BarChart  data={spentCategories.slice(0,5)}>
                            <XAxis 
                            dataKey="name" 
                            interval={0} 
                            style={{
                                fontFamily: "Inter",
                                //fontWeight: 250
                                }} 
                            stroke="black" 
                            tick={{ fill: 'black'}} 
                            tickFormatter={(value) => `${value.slice(0, 7)}...`} 
                            ></XAxis>
                            <YAxis stroke="black" tick={{ fill: 'black' }} />
                            
                            <Bar dataKey="total_spent" fill="#b684d8ff" >
                                {
                                    freqCategories.map((bar, index) => (
                                        <Cell key={index} fill={barColors[index % barColors.length]} />
                                        
                                    ))
                                }
                            </Bar> 
                        </BarChart>
                    </ResponsiveContainer>
                </div>
            </div>



            <div className={styles.catfreqChartContainer}>
                <div className={styles.title}>
                    Stores Most Spent At
                </div>
                <div className={styles.catfreqChart}>
                    <div className={styles.catFreqRank}>
            
                        {spentStores.slice(0,3).map((s, index) => (
                        <div key={index} className={styles.rankBox}>
                            <span className={`${styles.rankBadge} ${styles[`rank${index + 1}`]}`}>{index + 1}</span>
                            <span className={styles.rankLabel}>{s.name}</span>
                        </div>
                        
                        ))}
                    </div>
                    <ResponsiveContainer width="100%" height={300}>
                        <BarChart  data={spentStores}>
                            <XAxis 
                            dataKey="name" 
                            interval={0} 
                            style={{
                                fontFamily: "Inter",
                                //fontWeight: 250
                                }} 
                            stroke="black" 
                            tick={{ fill: 'black'}} 
                            tickFormatter={(value) => `${value.slice(0, 7)}...`} 
                            ></XAxis>
                            <YAxis stroke="black" tick={{ fill: 'black' }} />
                            
                            <Bar dataKey="total_spent" fill="#b684d8ff" >
                                {
                                    freqCategories.map((bar, index) => (
                                        <Cell key={index} fill={barColors[index % barColors.length]} />
                                        
                                    ))
                                }
                            </Bar> 
                        </BarChart>
                    </ResponsiveContainer>
                </div>
            </div>




        </div> 
        
    </div>
}

export default SummaryWidget;