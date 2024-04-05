import { useState } from 'react';
import { View, Text, TextInput, Image, TouchableOpacity, ScrollView, Linking } from "react-native";
import styles from './authentication.style';
import AppButton from '../../components/AppButton';
import GetAppWrapper from '../../components/GetAppWrapper';
import MiddleAccountWrapper from '../../components/MiddleAccountWrapper';
import AppText from '../../components/AppText.jsx';
import DatePicker from 'react-native-date-picker';
import { StackActions } from '@react-navigation/native';
import AwesomeAlert from 'react-native-awesome-alerts';

const BirthdayScreen = ({ navigation }) => {
    const [date, setDate] = useState(new Date())
    const [alert, showAlert] = useState(false);
    const popAction = StackActions.pop(2);
    return (
        <ScrollView>
            <View style={styles.parent}>
                <View style={styles.container}>
                    <Image
                        source={require("../../../assets/images/birthday.png")}
                    />
                    <AppText
                        fontType='bold'
                        style={{ color: '#73737C', fontSize: 18, textAlign: 'center', paddingHorizontal: 10, marginBottom: 10 }} >Add Your Birthday</AppText>
                    <AppText
                        style={{ color: '#73737C', fontSize: 14, textAlign: 'center', paddingHorizontal: 10, marginBottom: 2 }} >This won't be a part of your public profile.</AppText>
                    <TouchableOpacity onPress={() => {
                        showAlert(true)
                    }}>
                        <AppText
                            style={{ color: '#4CB5F9', fontSize: 14, textAlign: 'center', paddingHorizontal: 10, marginBottom: 10 }} >Why do I need to provide my birthday?</AppText>
                    </TouchableOpacity>

                    <DatePicker date={date} onDateChange={setDate} mode='date' />

                    <AppText
                        style={{ color: '#73737C', fontSize: 14, textAlign: 'center', paddingHorizontal: 10, marginVertical: 10 }} >You need to enter the date you were born</AppText>
                    <AppText
                        style={{ color: '#73737C', fontSize: 14, textAlign: 'center', paddingHorizontal: 10, marginBottom: 10 }} >Use your own birthday, even if this account is for a business, a pet, or something else</AppText>

                    <AppButton
                        buttonName={"Next"}
                        onPress={() => { console.log("Tap") }}
                    />
                    <TouchableOpacity onPress={() => { }}>
                        <AppText
                            style={{ color: '#4CB5F9', fontSize: 14, textAlign: 'center', paddingHorizontal: 10, marginBottom: 10 }} >Go Back</AppText>
                    </TouchableOpacity>
                </View>
                <MiddleAccountWrapper
                    name1={"Have an account?"}
                    name2={"Log in"}
                    onPress={() => {
                        navigation.navigate("LoginScreen");
                    }}
                />
                <GetAppWrapper />
                <AwesomeAlert
                    show={alert}
                    showProgress={false}
                    title='Birthdays'
                />
            </View>
        </ScrollView>
    )
};


export default BirthdayScreen;