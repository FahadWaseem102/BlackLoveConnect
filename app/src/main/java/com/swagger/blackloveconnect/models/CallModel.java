package com.swagger.blackloveconnect.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.telecom.Call;

public class CallModel implements Parcelable {
    String callID, callType, callerUID, receiverUID, status, roomID;
    long startTime, connectStartTime, callEndTime;

    public CallModel(){}

    protected CallModel(Parcel in) {
        callID = in.readString();
        callType = in.readString();
        callerUID = in.readString();
        receiverUID = in.readString();
        status = in.readString();
        roomID = in.readString();
        startTime = in.readLong();
        connectStartTime = in.readLong();
        callEndTime = in.readLong();
    }

    public static final Creator<CallModel> CREATOR = new Creator<CallModel>() {
        @Override
        public CallModel createFromParcel(Parcel in) {
            return new CallModel(in);
        }

        @Override
        public CallModel[] newArray(int size) {
            return new CallModel[size];
        }
    };

    public long getCallEndTime() {
        return callEndTime;
    }

    public void setCallEndTime(long callEndTime) {
        this.callEndTime = callEndTime;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getCallID() {
        return callID;
    }

    public void setCallID(String callID) {
        this.callID = callID;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallerUID() {
        return callerUID;
    }

    public void setCallerUID(String callerUID) {
        this.callerUID = callerUID;
    }

    public String getReceiverUID() {
        return receiverUID;
    }

    public void setReceiverUID(String receiverUID) {
        this.receiverUID = receiverUID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getConnectStartTime() {
        return connectStartTime;
    }

    public void setConnectStartTime(long connectStartTime) {
        this.connectStartTime = connectStartTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(callID);
        dest.writeString(callType);
        dest.writeString(callerUID);
        dest.writeString(receiverUID);
        dest.writeString(status);
        dest.writeString(roomID);
        dest.writeLong(startTime);
        dest.writeLong(connectStartTime);
        dest.writeLong(callEndTime);
    }
}
